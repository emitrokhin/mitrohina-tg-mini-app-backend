package ru.emitrohin.adminui.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.emitrohin.data.model.Role;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//TODO CSRF, CORS
//TODO Обобщить конфигурацию http
//TODO создать конфиг для https
//TODO пересмотреть Отключаение автоматическую регистрации JwtAuthenticationFilter/Admin
//TODO отключить стандартные /error и generated password is for development use only.
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> adminJwtAuthenticationFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false); //отключаем авто регистрацию контейнером.
        return registration;
    }

    @Bean
    @Order(1) //сперва определяем доступ для входа для всех
    public SecurityFilterChain authenticationFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/auth")
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request.requestMatchers("/api/auth/**").permitAll());
        return http.build();
    }

    @Bean
    @Order(2) //потом запрещаем доступ к неадминам к админке
    public SecurityFilterChain authenticatedAdminFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/admin/**")
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request ->
                        request.requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}