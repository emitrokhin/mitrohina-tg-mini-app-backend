package ru.emitrohin.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ru.emitrohin")
@EntityScan("ru.emitrohin.data.model")
public class UserUIApplication {

	//TODO тесты. все варианты
	public static void main(String[] args) {
		SpringApplication.run(UserUIApplication.class, args);
	}

}
