package org.example.api.service;

import org.example.api.domain.Coupon;
import org.example.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

	private final CouponRepository couponRepository;

	public ApplyService(CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}

	public void apply(Long userId) {
		long count = couponRepository.count();
		if (count > 100) {
			return;
		}

		couponRepository.save(new Coupon(userId));
	}
}
