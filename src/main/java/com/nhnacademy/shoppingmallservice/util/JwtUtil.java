package com.nhnacademy.shoppingmallservice.util;

import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final Key key;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtUtil(JwtProperties jwtProperties, RedisTemplate<String, String> redisTemplate) {
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;

        byte[] byteSecretKey = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String generateAccessToken(MemberDto memberDto) {
        return createJwt(memberDto.email(), memberDto.role(), jwtProperties.getExpirationTime());
    }

    public String generateRefreshToken(MemberDto memberDto) {
        long refreshExpirationTime = jwtProperties.getRefreshExpirationTime();
        return createJwt(memberDto.email(), memberDto.role(), refreshExpirationTime);
    }

    private String createJwt(String email, String role, Long expiredTime) {
        Claims claims = Jwts.claims();
//        claims.put("email", email);

        if (role != null) {
            claims.put("role", role);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Redis 에 저장된 RefreshToken 확인
    public boolean validateRefreshToken(String email, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get(getRefreshTokenKey(email));
        return storedToken != null && storedToken.equals(refreshToken);
    }

    // Access Token 검증
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Access Token 기한만료 확인
    public boolean isTokenExpired(String token) {
        return validateToken(token).getExpiration().before(new Date());
    }

    private String getRefreshTokenKey(String email) {
        return "refresh_token:" + email;
    }

    public Long getAccessTokenValidity() {
        return jwtProperties.getExpirationTime();
    }

    public Long getRefreshTokenValidity() {
        return jwtProperties.getRefreshExpirationTime();
    }

    public long getExpirationTime() {
        return jwtProperties.getExpirationTime();
    }
}
