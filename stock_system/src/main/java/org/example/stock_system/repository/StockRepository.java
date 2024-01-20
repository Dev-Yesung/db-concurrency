package org.example.stock_system.repository;

import org.example.stock_system.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

public interface StockRepository extends JpaRepository<Stock, Long> {

	// Spring Data Jpa 에서는 @Lock 애노테이션을 통해 쉽게 비관락 구현 가능
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Stock s WHERE s.id = :id")
	Stock findByIdWithPessimisticLock(Long id);

	@Lock(LockModeType.OPTIMISTIC)
	@Query("SELECT s FROM Stock s WHERE s.id = :id")
	Stock findByIdWithOptimisticLock(Long id);
}
