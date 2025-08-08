package com.example.demo.menuservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MenuServiceApplication.class)
class MenuServiceApplicationTests {

	@Test
	void happyPathTest() {
		// Simple happy path assertion
		assertThat(1 + 1).isEqualTo(2);
	}

}
