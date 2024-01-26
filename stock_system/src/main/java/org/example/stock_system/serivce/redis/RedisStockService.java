package org.example.stock_system.serivce.redis;

import org.example.stock_system.domain.Stock;
import org.example.stock_system.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
	레디스를 사용하여 동시성 문제를 해결할 때 사용하는 대표적인 두 개의 라이브러리
	1. Lettuce
	- setnx(set if not exist: key와 value를 se할 때, 기존의 값이 없을 때 set하는 명령어)
	명령어를 활용하여 분산락을 구현
	- 이 방식의 락은 spin lock 방식이므로 retry를 할 때 개발자가 직접 구현해야 한다.
	- spin lock이란 lock을 획득하려는 스레드가 락을 사용할 수 있는지 반복적으로 확인하면서
	락 획득을 시도하는 방식

	2. Redisson
	- pub-sub 기반(이벤트 기반)으로 Lock 제공
	- 채널을 하나 만들고 lock을 점유 중인 스레드가 lock을 획득하려고 대기 중인 스레드에게
	해제를 하면 알려줘 lock을 획득하게 만드는 방식
	- 별도의 retry 를 구현하지 않아도 된다.


	Lettuce vs Redisson
	둘의 차이는 락 재시도에 있다.
	Redisson의 경우 별도의 락 획득 로직을 구현 안해도 된다.
	반면, Lettuce는 락 획득 로직을 작성해야 한다.
	따라서 실무에서는 락을 다시 획득할 필요가 있는 경우 Redisson을 활용한다.
	그리고 락 획득 재시도가 필요 없는 경우 Lettuce를 활용한다.
	경우를 생각해서 이 둘을 잘 섞어서 사용하도록 하자.
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
