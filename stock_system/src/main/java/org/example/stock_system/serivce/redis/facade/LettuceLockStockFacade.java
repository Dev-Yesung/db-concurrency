package org.example.stock_system.serivce.redis.facade;

import org.example.stock_system.repository.RedisLockRepository;
import org.example.stock_system.serivce.mysql.NormalStockService;
import org.springframework.stereotype.Component;

/*

 */
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

	public void decrease(Long id, Long quantity) throws InterruptedException {
		while (!redisLockRepository.lock(id)) {
			// 레디스의 부하를 줄이기 위한 장치
			Thread.sleep(100);
		}

		try {
			normalStockService.decrease(id, quantity);
		} finally {
			redisLockRepository.unlock(id);
		}
	}
}
