package com.kumar.mithlesh.own;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ProductServiceApplication.class)
class ProductServiceApplicationTests {

	@Test
	void happyPathTest() {
		// Simple happy path assertion
		org.assertj.core.api.Assertions.assertThat(2 * 2).isEqualTo(4);
	}

}
