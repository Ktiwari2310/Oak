package com.oak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oak.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:oak;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver"
})
class OakApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void userIdIsGeneratedByTheServer() throws Exception {
		User user = new ObjectMapper().readValue("{\"id\":0}", User.class);

		assertNull(user.getId());
	}

}
