package org.example.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.Assertions;
import org.example.api.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplyServiceTest {

	@Autowired
	private ApplyService applyService;

	@Autowired
	private CouponRepository couponRepository;

	@DisplayName("쿠폰을 한 명만 발급")
	@Test
	void publishOnlyOnce() {
		// when
		applyService.apply(1L);
		long count = couponRepository.count();

		// then
		assertThat(count).isEqualTo(1);
	}

	@DisplayName("동시에 여러 명이 쿠폰을 발급")
	@Test
	void test() throws InterruptedException {
		// given
		int threadCount = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		// when
		CountDownLatch latch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			long userId = i;
			executorService.submit(() -> {
				try {
					applyService.apply(userId);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		long count = couponRepository.count();

		// then
		assertThat(count).isEqualTo(100);
	}

	@DisplayName("레디스를 활용해 동시에 여러 명이 쿠폰을 발급")
	@Test
	void useRedisToTest() throws InterruptedException {
		// given
		int threadCount = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		// when
		CountDownLatch latch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			long userId = i;
			executorService.submit(() -> {
				try {
					applyService.applyRedis(userId);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		long count = couponRepository.count();

		// then
		assertThat(count).isEqualTo(100);
	}
}
