package com.parakramaba.redispagination;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedisPaginationApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisPaginationApplication.class, args);
	}

}
