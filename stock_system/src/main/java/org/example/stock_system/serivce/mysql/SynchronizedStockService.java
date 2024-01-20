package org.example.stock_system.serivce.mysql;

import org.example.stock_system.domain.Stock;
import org.example.stock_system.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SynchronizedStockService {

	private final StockRepository stockRepository;

	public SynchronizedStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	// synchronized 키워드를 붙였음에도 정합성이 맞지 않는 이유는
	// @Transactional 이 프록시로 실행되기 때문이다.
	// 프록시로 실행되어 메소드가 종료될 때 데이터의 Commit 이 이뤄진다.
	// synchronized 된 메서드를 벗어나 하나의 트랜잭션이 커밋되기 전(프록시가 끝나기 전)에
	// 다른 스레드가 메서드에 접근해서 동작을 수행하면 DB는 아직 업데이트가 되지 않았으므로 정합성이 안맞게 된다.
	@Transactional
	public synchronized void decreaseOnlyByKeyword(Long id, Long quantity) {
		Stock stock = stockRepository.findById(id)
			.orElseThrow();
		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}

	// 프록시 객체가 생성되지 않아 데이터 정합성이 깨지지 않는 걸 확인할 수 있다.
	public synchronized void decreaseNoAnnotation(Long id, Long quantity) {
		Stock stock = stockRepository.findById(id)
			.orElseThrow();
		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
