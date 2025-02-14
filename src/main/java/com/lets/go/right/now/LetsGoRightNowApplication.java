package com.lets.go.right.now;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Entity Listener available
public class LetsGoRightNowApplication {

	public static void main(String[] args) {
		SpringApplication.run(LetsGoRightNowApplication.class, args);
	}

}
