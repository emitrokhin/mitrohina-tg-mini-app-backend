package ru.emitrohin.privateclubbackend.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;

@Configuration
@Profile("!test") //TODO должно быть как то по другому
public class TimeUtils {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}