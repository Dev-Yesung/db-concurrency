package org.example.stock_system.serivce.mysql;

import org.example.stock_system.domain.Stock;
import org.example.stock_system.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/*
	Named Lock 은 이름을 가진 메타데이터 락이다.
	해제할 때까지 다른 세션은 이 락을 획득 할 수 없다.
	주의할 점은 트랜잭션이 끝나도 바로 락이 해제되지 않아서
	별도의 명령어로 락을 해제하거나, 락의 선점시간이 끝나야 해제된다.
	MySQL 에서는 get_lock 명령어를 통해 네임드 락을 획득 할 수 있고
	release_lock 명령어를 통해 락을 해제할 수 있다.

	-> 기존에 비관락은 Stock 테이블 그 자체에 락을 걸었는데,
	네임드 락의 경우 테이블 자체에 거는 것이 아니라 별도의 공간에 락을 건다.

	이 예제에서는 편의성을 위해 JPA 의 native query 를 사용하여 구현
	또한 동일한 데이터 소스를 사용하여 구현함
	하지만 실제로 사용할 때는 데이터 소스를 분리해서 사용한다.
	같은 데이터 소스를 사용하면 커넥션 풀이 부족해지는 현상으로 인해
	다른 서비스에도 영향을 끼칠 수 있기 때문이다.
 */
@Service
public class NamedLockStockService {

	private final StockRepository stockRepository;

	public NamedLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	// Named Lock 은 부모의 트랜잭션과 별도로 트랜잭션이 수행되어야 하므로
	// Propagation Level 을 변경한다.

	// Named Lock 은 분산락을 구현할 때 사용한다.

	// 비관락은 time out을 구현하기 힘들지만, Named Lock은 time out을 손쉽게 구현할 수 있다.
	// 그 이외에 데이터를 삽입시 정합성을 맞춰야 할 때도 Named Lock을 사용할 수 있다.

	// 하지만 트랜잭션 종료시 Lock 해제와 세션관리를 잘해줘야 하므로 주의해서 사용해야 하므로
	// 실제 사용할 때는 구현 방법이 복잡할 것이다.
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findById(id)
			.orElseThrow();
		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
