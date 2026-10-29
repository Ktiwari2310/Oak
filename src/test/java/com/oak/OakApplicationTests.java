package com.oak;

import com.oak.request.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
	void createUserRequestDoesNotExposeServerManagedFields() {
		var fields = Arrays.stream(CreateUserRequest.class.getRecordComponents())
				.map(component -> component.getName())
				.toList();

		assertFalse(fields.contains("id"));
		assertFalse(fields.contains("createdAt"));
		assertFalse(fields.contains("isAdmin"));
	}

}
