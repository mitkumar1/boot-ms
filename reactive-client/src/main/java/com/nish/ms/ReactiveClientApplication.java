package com.nish.ms;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ReactiveClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveClientApplication.class, args);
	}


	@Bean
	@LoadBalanced
	public WebClient.Builder builder(){

		return WebClient.builder();
	}

	@Bean
	public WebClient client(@Qualifier("builder") WebClient.Builder builderRef) {

		return builderRef.build();
	}
}
