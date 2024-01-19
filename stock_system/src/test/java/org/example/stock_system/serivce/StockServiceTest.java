package org.example.stock_system.serivce;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.example.stock_system.domain.Stock;
import org.example.stock_system.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockServiceTest {

	@Autowired
	private StockService stockService;
	@Autowired
	private StockRepository stockRepository;

	@BeforeEach
	public void before() {
		stockRepository.saveAndFlush(new Stock(1L, 100L));
	}

	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}

	@DisplayName("상품의 재고 1개 감소")
	@Test
	void decreaseStock() {
		// when
		stockService.decrease(1L, 1L);
		Stock stock = stockRepository.findById(1L)
			.orElseThrow();
		Long quantity = stock.getQuantity();

		// then
		assertThat(quantity).isEqualTo(99);
	}

	@DisplayName("동시에 100개의 요청")
	@Test
	void request100QuantityConcurrently() throws InterruptedException {
		// given
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					stockService.decrease(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		Stock stock = stockRepository.findById(1L)
			.orElseThrow();
		Long quantity = stock.getQuantity();

		// then
		assertThat(quantity).isEqualTo(0);
	}

}
