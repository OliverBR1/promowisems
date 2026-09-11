package tech.oliver.promowisems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PromowisemsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PromowisemsApplication.class, args);
	}

}
