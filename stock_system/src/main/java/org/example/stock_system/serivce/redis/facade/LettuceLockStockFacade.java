package org.example.stock_system.serivce.redis.facade;

import org.example.stock_system.repository.RedisLockRepository;
import org.example.stock_system.serivce.mysql.NormalStockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

	private final RedisLockRepository redisLockRepository;
	private final NormalStockService normalStockService;

	public LettuceLockStockFacade(
		RedisLockRepository redisLockRepository,
		NormalStockService normalStockService
	) {
		this.redisLockRepository = redisLockRepository;
		this.normalStockService = normalStockService;
	}


	/*
		재고 감소 명령어 전,후로 락을 걸어야하기 때문에 퍼사드 클래스를 활용한다.

		Lettuce 구현체를 이용하여 락을 거는 방법은 사용이 간단하다는 장점이 있다.
		하지만 스핀락 방식으로 락을 획득하기 때문에, 레디스에 부하를 준다.
		따라서 락 획득에 텀을 줘서 부하를 줄여야 한다.
	 */
	public void decrease(Long id, Long quantity) throws InterruptedException {
		// 동시성 제어를 위해 락을 건다.
		while (!redisLockRepository.lock(id)) {

			// 스핀락 방식으로 동작한다. 레디스의 부하를 줄이기 위해 스레드 슬립을 넣었다.
			Thread.sleep(100);
		}

		try {
			// 비즈니스 로직을 수행한다.
			normalStockService.decrease(id, quantity);
		} finally {
			// 모든 로직을 수행한 후에는 락을 풀어준다.
			redisLockRepository.unlock(id);
		}
	}
}
