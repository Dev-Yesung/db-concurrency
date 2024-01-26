package org.example.stock_system.serivce.redis.facade;

import java.util.concurrent.TimeUnit;

import org.example.stock_system.serivce.mysql.NormalStockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
	Redisson 구현체는 자신이 점유하고 있는 락을 해제할 때,
	채널에 메시지를 보내줌으로써 락을 획득해야 하는 스레드에게 락을 획득하라고 메시지를 보낸다.
	이벤트 기반의 락 획득이라 분산환경에 적합하다.
	또한 이벤트 기반이라 스핀락에 비해 레디스에 부하를 덜 준다.
 */
@Component
public class RedissonLockStockFacade {

	private static final Logger log = LoggerFactory.getLogger(RedissonLockStockFacade.class);

	private final RedissonClient redissonClient;
	private final NormalStockService normalStockService;

	public RedissonLockStockFacade(
		RedissonClient redissonClient,
		NormalStockService normalStockService
	) {
		this.redissonClient = redissonClient;
		this.normalStockService = normalStockService;
	}

	public void decrease(Long id, Long quantity) throws InterruptedException {
		RLock lock = redissonClient.getLock(id.toString());
		try {
			/*
				몇 초 동안 락 획득을 시도할 것인지 -> 10초
				몇 초 동안 락을 해제할 것인지 -> 1초
				시간 단위 -> 초 단위
				만일 락을 획득하는데 실패하는 경우에는 락 획득시간을 늘려본다.
			 */
			boolean available = lock.tryLock(15, 1, TimeUnit.SECONDS);
			if (!available) {
				log.info("Lock 획득 실패");
				return;
			}

			// 락을 획득했다면, 비즈니스 로직을 수행한다.
			normalStockService.decrease(id, quantity);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}
}
