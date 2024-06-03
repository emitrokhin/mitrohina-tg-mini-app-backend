package ru.emitrohin.privateclubbackend.service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
//TODO класс прямая калька с предыдущей версии JJWT.
public class JwtService {

    @Value("${jwt.key}")
    private String jwtSigningKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    private final Clock clock;

    public long extractTelegramId(String token) {
        return Long.parseLong(extractClaim(token, DecodedJWT::getSubject));
    }

    public UUID extractAdminId(String token) {
        return UUID.fromString(extractClaim(token, DecodedJWT::getSubject));
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSigningKey);
    }

    private DecodedJWT decodeToken(String token) {
        JWTVerifier.BaseVerification verification = (JWTVerifier.BaseVerification) JWT.require(getAlgorithm());
        return verification.build(clock).verify(token);
    }

    private <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolver) {
        final DecodedJWT jwt = decodeToken(token);
        return claimsResolver.apply(jwt);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt);
    }

    public boolean tokenIsNotExpired(String token) {
        return !extractExpiration(token).before(Date.from(clock.instant()));
    }

    public boolean isTokenValid(String token, long telegramId) {
        return (extractTelegramId(token) == telegramId) && tokenIsNotExpired(token);
    }

    public boolean isAdminTokenValid(String token, UUID adminId) {
        try {
            return (extractAdminId(token).equals(adminId)) && tokenIsNotExpired(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateTokenForTelegramId(Long telegramId) {
        return generateToken(String.valueOf(telegramId));
    }

    public String generateTokenForUUID(UUID id) {
        return generateToken(id.toString());
    }

    private String generateToken(String subject) {
        Instant now = clock.instant();
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(validityInMilliseconds)))
                .sign(Algorithm.HMAC256(jwtSigningKey));
    }
}