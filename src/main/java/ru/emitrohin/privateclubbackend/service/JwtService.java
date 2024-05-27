package ru.emitrohin.privateclubbackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

//TODO А правильно ли размещен? Это точно сервис?
@Service
public class JwtService {

    @Value("${jwt.key}")
    private String jwtSigningKey;

    @Value("${jwt.expiration}")
    //TODO задействовать, никак не использется пока
    private long validityInMilliseconds;

    public long extractTelegramId(String token) {
        String claim = extractClaim(token, Claims::getSubject);
        return Long.parseLong(claim);
    }

    public UUID extractAdminId(String token) {
        String claim = extractClaim(token, Claims::getSubject);
        return UUID.fromString(claim);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isTokenValid(String token, long telegramId) {
        long extractTelegramId = extractTelegramId(token);
        return ( (extractTelegramId == telegramId) && !isTokenExpired(token));
    }

    public Boolean isAdminTokenValid(String token, UUID adminId) {
        try {
            UUID extractedAdminId = extractAdminId(token);
            return ((extractedAdminId.equals(adminId)) && !isTokenExpired(token));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateTokenForTelegramId(Long telegramId){
        return generateTokenForString(String.valueOf(telegramId));
    }

    public String generateTokenForUUID(UUID id){
        return generateTokenForString(id.toString());
    }

    private String generateTokenForString(String token){
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().claims(claims)
                .subject(token)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + validityInMilliseconds))
                .signWith(getSignKey(), Jwts.SIG.HS256).compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
