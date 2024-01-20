package org.example.stock_system.serivce.facade;

import org.example.stock_system.serivce.OptimisticLockStockService;
import org.springframework.stereotype.Component;

/*
	OptimisticLock 은 실제 락을 이용하는 것이 아니라, 버전관리를 통해 락을 구현
	데이터의 버전 충돌이 생기면 애플리케이션 레벨에서 로직을 다시 수행하도록 만들어야 한다.
	OptimisticLock 은 실패했을 때 재시도(Retry)를 해야한다.
	여기서는 퍼사드 클래스를 만들고 퍼사드에서 while 문으로 반복하여 구현한다.

	장점은 실제 락을 잡는 것이 아니기 때문에 성능상 비관락보다 낫지만
	단점은 개발자가 직업 반복로직을 구현해야 한다는 점에 있다.
	그리고 충돌이 빈번하게 일어날거 같다면 비관락이 낫다.
	잘 구분해서 사용하도록 하자.
 */
@Component
public class OptimisticLockStockFacade {

	private final OptimisticLockStockService optimisticLockStockService;

	public OptimisticLockStockFacade(OptimisticLockStockService optimisticLockStockService) {
		this.optimisticLockStockService = optimisticLockStockService;
	}

	public void decrease(Long id, Long quantity) throws InterruptedException {
		while (true) {
			try {
				optimisticLockStockService.decrease(id, quantity);
				break;
			} catch (Exception e) {
				Thread.sleep(50);
			}
		}
	}
}
