package ru.emitrohin.userapi.services;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.emitrohin.userapi.config.properties.JWTProperties;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@EnableConfigurationProperties(JWTProperties.class)
@RequiredArgsConstructor
public class JwtService {

    private final JWTProperties properties;

    private final Clock clock;

    public long extractTelegramUserId(String token) {
        return Long.parseLong(extractClaim(token, DecodedJWT::getSubject));
    }

    public boolean tokenIsNotExpired(String token) {
        var date = extractClaim(token, DecodedJWT::getExpiresAt);
        return !date.before(Date.from(clock.instant()));
    }

    public boolean isTelegramUserTokenValid(String token, long telegramId) {
        return (extractTelegramUserId(token) == telegramId) && tokenIsNotExpired(token);
    }

    private <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolver) {
        var verification = (JWTVerifier.BaseVerification) JWT.require(Algorithm.HMAC256(properties.key()));
        var jwt = verification.build(clock).verify(token);
        return claimsResolver.apply(jwt);
    }

    public String generateTokenForTelegramUserId(Long telegramUserId) {
        return generateToken(String.valueOf(telegramUserId));
    }

    private String generateToken(String subject) {
        Instant now = clock.instant();
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(properties.expiration())))
                .sign(Algorithm.HMAC256(properties.key()));
    }
}