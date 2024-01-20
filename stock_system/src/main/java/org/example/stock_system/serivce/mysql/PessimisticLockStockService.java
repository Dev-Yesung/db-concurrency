package org.example.stock_system.serivce.mysql;

import org.example.stock_system.domain.Stock;
import org.example.stock_system.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
	synchronized 키워드의 문제점은 오직 하나의 서버일 때만 유효한 해결방법이다.
	서버가 두 대, 혹은 그 이상일 경우 여러 컴퓨터가 DB에 접근하게 된다.
	만일 서버1에서 데이터를 가져가 갱신을 하고 있는데,
	그 사이에 커밋되지 않은 데이터를 서버2가 가져가서 작업을 한다면?
	synchronized 는 각 프로세스 안에서는 보장이 되지만, 그 영향이 DB 에는 영향을 끼치지 못한다.
	즉, DB 수준에서는 여러 스레드가 접근하여 데이터를 커밋하게 되므로 race condition 이 발생하게 된다.
	DB 수준에서 Race Condition 에 관한 해결을 해야한다.
*/

/*
	[ Mysql 기준 ]

	1. Pessimistic Lock
	: 실제로 데이터에 Lock을 걸어서 정합성을 맞추는 방법
	exclusive lock을 걸면 다른 트랜잭션에서는 lock이 해제되기 전에
	데이터를 가져갈 수 없다. 데드락이 걸릴 수 있기 때문에 주의해야 한다.

	2. Optimistic Lock
	: 실제로 Lock을 이용하지 않고 버전(version)을 이용함으로써 정합성을 맞추는 방법
	먼저 데이터를 읽은 후에 업데이트를 수행할 때, 현재 읽은 버전과 일치하는지 확인하며 업데이트
	만일, 읽은 버전에서 수정 사항이 생겼을 경우 애플리케이션 레벨에서 다시 데이터를 읽은 후
	작업(로직을 직접 구현해서)을 수행해야 한다.

	3. Named Lock
	: 이름을 가진 metadata locking이다.
	이름을 가진 lock을 획득한 후 해제할 때까지 다른 세션은 lock을 획득할 수 없도록 한다.
	주의사항은 트랜잭션이 종료될 때 락이 자동으로 해제되지 않는다.
	따라서 별도의 명령어로 해제를 수행하거나 선점시간이 끝나야 해제된다.

	이 락은 pessimistic lock과 유사한데, 차이점이 있다면
	pessimistic lock은 데이터베이스의 테이블이나 테이블의 row 단위로 락을 건다.
	Named Lock은 메타데이터에 락을 거는 방법이다.
 */

/*
	Pessimistic Lock 을 이용한 문제해결에서 비관락은 실제로 락을 걸기 때문에
	일반적으로 낙관락보다 성능은 떨어짐(Latency, TPS)
	하지만 충돌이 빈번한 곳에 비관락을 사용하게 되면 오히려 낙관락보다 성능이 좋음
	따라서 케이스를 잘 분류하여 비관락과 낙관락을 선택해서 사용한다.
 */
@Service
public class PessimisticLockStockService {

	private final StockRepository stockRepository;

	public PessimisticLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findByIdWithPessimisticLock(id);
		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}
}
