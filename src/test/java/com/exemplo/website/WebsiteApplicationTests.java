package com.exemplo.website;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfSystemProperty(named = "aiven.integration", matches = "true")
class WebsiteApplicationTests {

	@Test
	void contextLoads() {
	}

}
