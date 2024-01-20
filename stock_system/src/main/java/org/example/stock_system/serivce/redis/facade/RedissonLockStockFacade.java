package org.example.stock_system.serivce.redis.facade;

import org.example.stock_system.repository.RedisLockRepository;
import org.example.stock_system.serivce.mysql.NormalStockService;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class RedissonLockStockFacade {

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
		while (!redissonClient.lock(id)) {
			// 레디스의 부하를 줄이기 위한 장치
			Thread.sleep(100);
		}

		try {
			normalStockService.decrease(id, quantity);
		} finally {
			redissonClient.unlock(id);
		}
	}
}
