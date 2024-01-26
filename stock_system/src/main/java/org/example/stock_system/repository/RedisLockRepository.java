package org.example.stock_system.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisLockRepository {

	private RedisTemplate<String, String> redisTemplate;

	public RedisLockRepository(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/* 	redis의 setnx 명령어를 사용하여 구현한다.
	 	setnx는 key 값이 이미 존재할 경우 값을 세팅하는데 실패한다.
	 	따라서 lock의 역할을 할 수 있다.
	*/
	public Boolean lock(Long key) {
		return redisTemplate
			.opsForValue()
			.setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
	}

	public Boolean unlock(Long key) {
		return redisTemplate.delete(generateKey(key));
	}

	private String generateKey(Long key) {
		return key.toString();
	}
}
