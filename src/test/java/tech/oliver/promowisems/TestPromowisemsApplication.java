package tech.oliver.promowisems;

import org.springframework.boot.SpringApplication;

import static tech.oliver.promowisems.ContainerConfig.getProperties;
import static tech.oliver.promowisems.ContainerConfig.wireMockContainer;

public class TestPromowisemsApplication {

	public static void main(String[] args) {
		wireMockContainer.start();
		getProperties().forEach(System::setProperty);
		SpringApplication.from(PromowisemsApplication::main)
				.with(ServiceConnectionConfig.class).run(args);
	}

}
