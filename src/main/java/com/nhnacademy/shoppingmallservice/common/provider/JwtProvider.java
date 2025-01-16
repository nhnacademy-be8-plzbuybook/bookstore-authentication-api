package com.nhnacademy.shoppingmallservice.common.provider;

import com.nhnacademy.shoppingmallservice.common.exception.InvalidTokenException;
import com.nhnacademy.shoppingmallservice.dto.MemberDto;
import com.nhnacademy.shoppingmallservice.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final Key key;

    @Autowired
    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        byte[] byteSecretKey = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String generateAccessToken(MemberDto memberDto) {
        if (memberDto == null) {
            throw new IllegalArgumentException("invalid MemberDto");
        }
        return createJwt(memberDto.email(), memberDto.role(), memberDto.memberStateName(), jwtProperties.getAccessExpirationTime());
    }

    public String generateRefreshToken(MemberDto memberDto) {
        if (memberDto == null) {
            throw new IllegalArgumentException("invalid MemberDto");
        }
        return createJwt(memberDto.email(), memberDto.role(), memberDto.memberStateName(), jwtProperties.getAccessExpirationTime());
    }

    private String createJwt(String email, String role, String memberStateName, Long expiredTime) {
        if (email.isBlank() || role.isBlank() || memberStateName.isBlank() || expiredTime == null) {
            throw new IllegalArgumentException("invalid parameters for creating JWT");
        }

        Claims claims = Jwts.claims();
        claims.put("role", role);
        claims.put("memberStateName", memberStateName); // 클레임 추가

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //이메일 가져오기
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우 예외가 발생하지만, 여기서 이메일을 추출하여 반환
            Claims claims = e.getClaims(); // 만료된 토큰의 claims를 통해 이메일 추출
            return claims.getSubject(); // 이메일 반환
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid JWT token", e);
        }
    }

    // 토큰 만료, 변조, 잘못된형식 분리해서 생각?
    public void validateToken(String token) {
        try {
            Claims claims = parseToken(token);

            // 필수 클레임 검증
            if (claims.get("role") == null) {
                throw new InvalidTokenException("JWT token is missing 'role' claim", null);
            }
            if (claims.get("memberStateName") == null) {
                throw new InvalidTokenException("JWT token is missing 'memberStateName' claim", null);
            }

        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Expired JWT token", e);
        } catch (SignatureException e) {
            throw new InvalidTokenException("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Malformed JWT token", e);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid token", e);
        }
    }

    public String getRefreshTokenKey(String email) {
        return "refresh_token:" + email;
    }

    public Long getAccessExpirationTime() {
        return jwtProperties.getAccessExpirationTime();
    }

    public Long getRefreshExpirationTime() {
        return jwtProperties.getRefreshExpirationTime();
    }
}
