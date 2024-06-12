package ru.emitrohin.adminui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.emitrohin.adminui.config.FixedClockConfig;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(FixedClockConfig.class)
public class FixedClockConfigTest {

    @Autowired
    private Clock clock;

    @Test
    public void testClockBean() {
        assertThat(clock).as("Clock bean should not be null").isNotNull();

        var expectedInstant = Instant.ofEpochSecond(1717001010L);
        assertThat(expectedInstant).as("Clock instant should match the fixed instant").isEqualTo(clock.instant());
        assertThat(ZoneId.of("UTC")).as("Clock zone should be UTC").isEqualTo(clock.getZone());
    }
}

