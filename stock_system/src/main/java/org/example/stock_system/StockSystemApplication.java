package org.example.stock_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
	MySQL vs Redis 선택 가이드
	MySQL 은 기존에 DBMS로 사용할 경우 그대로 사용할 수 있다는 장점이 있다.
	또한 어느 정도의 트래픽은 방어할 수 있기 때문에 심각한 트래픽 발생 상황이 아니면 사용하자.

	반면 Redis는 별도의 구축 비용이 들겠지만 많은 양의 트래픽을 커버할 수 있다.
	또한 MySQL 보다 성능이 좋다. 따라서 심각한 트래픽이 발생할 경우 사용하도록 하자.
 */
@SpringBootApplication
public class StockSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockSystemApplication.class, args);
	}

}
