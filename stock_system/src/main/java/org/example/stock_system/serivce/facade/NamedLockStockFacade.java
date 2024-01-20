package org.example.stock_system.serivce.facade;

import org.example.stock_system.repository.LockRepository;
import org.example.stock_system.serivce.NamedLockStockService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 실제 로직 전후로 락획득 해제를 해야하므로 퍼사드 클래스를 추가한다.
// AOP 로 프록시 객체 만들어서 해도 가능할 듯..?
@Component
public class NamedLockStockFacade {

	private final LockRepository lockRepository;
	private final NamedLockStockService namedLockStockService;

	public NamedLockStockFacade(LockRepository lockRepository, NamedLockStockService stockService) {
		this.lockRepository = lockRepository;
		this.namedLockStockService = stockService;
	}

	@Transactional
	public void decrease(Long id, Long quantity) {
		try {
			lockRepository.getLock(id.toString());
			namedLockStockService.decrease(id, quantity);
		} finally {
			lockRepository.releaseLock(id.toString());
		}
	}

}
