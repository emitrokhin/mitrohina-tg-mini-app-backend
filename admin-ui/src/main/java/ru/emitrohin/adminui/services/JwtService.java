package ru.emitrohin.adminui.services;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.emitrohin.adminui.config.properties.JWTProperties;

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

    public UUID extractAdminId(String token) {
        return UUID.fromString(extractClaim(token, DecodedJWT::getSubject));
    }

    public boolean tokenIsNotExpired(String token) {
        var date = extractClaim(token, DecodedJWT::getExpiresAt);
        return !date.before(Date.from(clock.instant()));
    }

    public boolean isAdminTokenValid(String token, UUID adminId) {
        try {
            return (extractAdminId(token).equals(adminId)) && tokenIsNotExpired(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolver) {
        var verification = (JWTVerifier.BaseVerification) JWT.require(Algorithm.HMAC256(properties.key()));
        var jwt = verification.build(clock).verify(token);
        return claimsResolver.apply(jwt);
    }

    public String generateTokenForUUID(UUID id) {
        return generateToken(id.toString());
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