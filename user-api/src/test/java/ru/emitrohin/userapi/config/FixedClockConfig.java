package ru.emitrohin.userapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.*;

@TestConfiguration
public class FixedClockConfig {

    //значение взято из initData Telegram Webapp
    private final static Instant INSTANT = Instant.ofEpochSecond( 1717001010L );

    @Bean("fixedClock")
    @Primary
    public Clock clock() {
        return Clock.fixed(INSTANT, ZoneId.of("UTC"));
    }
}
