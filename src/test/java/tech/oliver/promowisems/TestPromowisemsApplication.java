package tech.oliver.promowisems;

import org.springframework.boot.SpringApplication;

public class TestPromowisemsApplication {

	public static void main(String[] args) {
		SpringApplication.from(PromowisemsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
