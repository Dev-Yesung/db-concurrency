package org.example.api.service;

import org.example.api.domain.Coupon;
import org.example.api.repository.CouponCountRepository;
import org.example.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

/*
	Race Condition이 일어나는 부분은 쿠폰의 개수를 가져오는 부분이었다.
	Race Condition이 발생하는 부분은 여러 스레드가 동시에 공유 자원에 접근할 때이다.
	발급할 수 있는 쿠폰의 총 개수는 공유 자원에 해당한다.

	만일 싱글 스레드로 작업을 하게 된다면 Race condition은 일어나지 않을 것이다.
	하지만 쿠폰 발급 전체를 싱글 스레드로 작업한다면? Throughput이 좋지 않을 것이다.
	먼저 쿠폰 발급을 요청한 사람이 발급을 모두 끝내야 그 다음 사람이 가능하기 때문이다.

	Race condition 을 해결할 첫 번째 방법으로 자바에서 지원하는 synchronized 키워드를 생각할 수 있지만,
	이 키워드는 하나의 프로세스 내에서만 유효하기 때문에 분산 환경(다수의 서버)에서는 이용할 수 없다.

	또 다른 방법으로는 MySQL의 락을 활용한 방법, Redis의 락을 활용한 방법이 있다.
	하지만 잘 생각해보자. 우리가 정합성이 필요한 부분은 쿠폰의 발급 개수뿐이다.
	만일, MySQL의 락을 사용한다면, 쿠폰 개수를 가져오는 것부터 쿠폰을 생성할 때까지 락이 걸려야 한다.
	이렇게 될 경우 락을 거는 구간이 길어져 성능 하락이 발생한다.

	쿠폰 발급의 핵심은 한정된 개수의 쿠폰을 발급하는 것이다.
	즉, 쿠폰 개수에 관한 정합성만 관리하면 그만이다.

	Redis의 incr 명령어는 특정 키의 value를 1씩 증가시키는 명령어이다.
	레디스는 싱글 스레드로 동작하여 레이스 컨디션을 해결할 뿐만 아니라
	incr 명령어는 속도도 빠르기 때문에,
	throughput에 손실도 없고 레이스 컨디션도 해결할 수 있다.
 */
@Service
public class ApplyService {

	private final CouponRepository couponRepository;
	private final CouponCountRepository couponCountRepository;

	public ApplyService(
		CouponRepository couponRepository,
		CouponCountRepository couponCountRepository
	) {
		this.couponRepository = couponRepository;
		this.couponCountRepository = couponCountRepository;
	}

	/*
		이 로직에서 발생할 수 있는 문제점은 무엇일까?
		얼핏 보면 문제가 없어보이지만, 발급하는 쿠폰의 개수가 많아질수록 RDB에 부하가 심해진다.
		만일 쿠폰 전용 RDB라면 그나마 낫지만,
		다른 서비스에서도 사용한다면 쿠폰 서비스에 의해 다른 서비스에도 장애가 발생할 수 있다.

		만일 1분에 100개의 insert가 가능한 RDB가 있다고 하자.
		갑자기 쿠폰 생성 10000개가 요청으로 들어왔다고 하자.
		이것을 처리하는데 100분이 걸려서 나머지 요청은 그 이후 혹은 timeout 된다.
		또한 이 요청을 처리하는데 부하가 걸려서 장애로 이어진다.
	 */
	public void applyRedis(Long userId) {
		// redis에서 쿠폰발급을 위해 개수를 가져온다.
		Long count = couponCountRepository.increment();

		// 만일 100이 넘었다면 쿠폰 발급을 안하면 된다.
		if (count > 100) {
			return;
		}

		// 100을 넘지 않았다면 쿠폰을 발급해서 mysql에 저장한다.
		couponRepository.save(new Coupon(userId));
	}

	public void apply(Long userId) {
		long count = couponRepository.count();
		if (count > 100) {
			return;
		}

		couponRepository.save(new Coupon(userId));
	}
}
