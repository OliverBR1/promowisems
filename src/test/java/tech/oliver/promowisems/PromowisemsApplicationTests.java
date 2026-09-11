package tech.oliver.promowisems;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(ServiceConnectionConfig.class)
@SpringBootTest
class PromowisemsApplicationTests {

	@Test
	void contextLoads() {
	}

}
