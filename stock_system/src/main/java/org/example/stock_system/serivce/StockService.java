package org.example.stock_system.serivce;

import org.example.stock_system.domain.Stock;
import org.example.stock_system.repository.StockRepository;
import org.springframework.stereotype.Service;

@Service
public class StockService {

	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	public void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findById(id)
			.orElseThrow();
		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}
}
