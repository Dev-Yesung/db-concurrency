package org.example.stock_system.serivce.redis;

import org.example.stock_system.domain.Stock;
import org.example.stock_system.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
	레디스를 사용하여 동시성 문제를 해결할 때 사용하는 대표적인 두 개의 라이브러리
	1. Lettuce

	2. Redisson

 */
@Service
public class RedisStockService {

	private final StockRepository stockRepository;

	public RedisStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findById(id)
			.orElseThrow();
		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
