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
import java.util.function.Function;

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

    public String generateToken(Long telegramId){
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder().claims(claims)
                .subject(String.valueOf(telegramId))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(getSignKey(), Jwts.SIG.HS256).compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
