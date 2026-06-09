package com.tickets.payment_gateway_mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PaymentGatewayMockApplication {

@Bean
public org.springframework.web.client.RestTemplate restTemplate() {
    return new org.springframework.web.client.RestTemplate();
}

	public static void main(String[] args) {
		SpringApplication.run(PaymentGatewayMockApplication.class, args);
	}

}
