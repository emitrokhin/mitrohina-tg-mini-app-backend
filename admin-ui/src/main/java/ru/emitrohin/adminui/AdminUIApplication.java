package ru.emitrohin.adminui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ru.emitrohin")
@EntityScan("ru.emitrohin.data.model")
public class AdminUIApplication {

	//TODO тесты. все варианты
	public static void main(String[] args) {
		SpringApplication.run(AdminUIApplication.class, args);
	}

}
