package ru.emitrohin.adminui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ru.emitrohin")
@EntityScan("ru.emitrohin.data.model")
@Theme("admin-ui")
public class AdminUIApplication implements AppShellConfigurator {

	//TODO тесты. все варианты
	public static void main(String[] args) {
		SpringApplication.run(AdminUIApplication.class, args);
	}

}
