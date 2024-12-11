package com.nhnacademy.shoppingmallservice.controller;

import com.nhnacademy.shoppingmallservice.User.User;
import com.nhnacademy.shoppingmallservice.dto.LoginRequest;
import com.nhnacademy.shoppingmallservice.dto.TokenResponse;
import com.nhnacademy.shoppingmallservice.properties.JwtProperties;
import com.nhnacademy.shoppingmallservice.service.MemberService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

//    private final Map<String, User> temp = Map.of(
//            "admin", User.createAdmin("admin", "관리자", "1234"),
//            "user", User.createUser("user", "사용자", "1234")
//    );  // DB 대신 임시로 사용할 유저정보
//
//    private final JwtProperties jwtProperties;
//
//    public TestController(JwtProperties jwtProperties) {
//        this.jwtProperties = jwtProperties;
//    }
//
//    @PostMapping("/api/account/login")
//    public ResponseEntity<TokenResponse> doLogin(@RequestParam LoginRequest loginRequest) {
//        // 사용자 인증 로직 (간단한 예)
//        if ("user".equals(loginRequest.getId()) && "password".equals(loginRequest.getPassword())) {
//            String token = Jwts.builder()
//                    .setSubject(loginRequest.getId())
//                    .claim("roles", List.of("ROLE_USER"))
//                    .setIssuedAt(new Date())
//                    .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime()))
//                    .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret().getBytes())
//                    .compact();
//
//            TokenResponse response = new TokenResponse(token, "Bearer", jwtProperties.getExpirationTime());
//            return ResponseEntity.ok(response);
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }

}
