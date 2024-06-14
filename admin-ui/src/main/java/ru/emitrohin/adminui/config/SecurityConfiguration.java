package ru.emitrohin.adminui.config;


import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import ru.emitrohin.adminui.views.LoginView;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//TODO CSRF, CORS
//TODO Обобщить конфигурацию http
//TODO создать конфиг для https
public class SecurityConfiguration extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                authorize -> authorize.requestMatchers(antMatchers("/images/*.png")).permitAll());

        // Icons from the line-awesome addon
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(antMatchers("/line-awesome/**/*.svg")).permitAll());

        super.configure(http);
        setLoginView(http, LoginView.class);
    }
}