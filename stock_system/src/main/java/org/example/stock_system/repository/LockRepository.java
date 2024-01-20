package org.example.stock_system.repository;

import org.example.stock_system.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// 편의상 똑같은 stock 엔티티를 사용하지만
// 실무에서는 별도의 Jdbc 를 사용해야 한다.
public interface LockRepository extends JpaRepository<Stock, Long> {

	// NamedLock 으로 락 획득
	@Query(value = "SELECT GET_LOCK(:key, 3000)", nativeQuery = true)
	void getLock(String key);

	// NamedLock 으로 락 해제
	@Query(value = "SELECT RELEASE_LOCK(:key)", nativeQuery = true)
	void releaseLock(String key);
}
