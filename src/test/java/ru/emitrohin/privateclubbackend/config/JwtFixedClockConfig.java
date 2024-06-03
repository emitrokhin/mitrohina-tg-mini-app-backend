package ru.emitrohin.privateclubbackend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.*;

@TestConfiguration
@Profile("test")
public class JwtFixedClockConfig {

    //значение взято из initData Telegram Webapp
    private final static Instant instant = Instant.ofEpochSecond( 1717001010L );

    @Bean
    @Primary
    public Clock clock() {
        return Clock.fixed(instant, ZoneId.of("UTC"));
    }
}
