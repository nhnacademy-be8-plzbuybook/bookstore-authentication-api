//package com.nhnacademy.shoppingmallservice.security.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nhnacademy.shoppingmallservice.dto.LoginRequest;
//import com.nhnacademy.shoppingmallservice.dto.TokenResponse;
//import com.nhnacademy.shoppingmallservice.properties.JwtProperties;
//import com.nhnacademy.shoppingmallservice.security.details.PrincipalDetails;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Calendar;
//import java.util.Date;
//
//@Slf4j
//public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager authenticationManager;
//    private final JwtProperties jwtProperties;
//    private final ObjectMapper objectMapper;
//
//
//    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtProperties jwtProperties, ObjectMapper objectMapper) {
////        super(authenticationManager);
//        this.authenticationManager = authenticationManager;
//        this.jwtProperties = jwtProperties;
//        this.objectMapper = objectMapper;
//        setFilterProcessesUrl("/api/**");
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        if (request.getRequestURI().startsWith("/api/login")) {
//            return super.attemptAuthentication(request, response);
//        }
//        LoginRequest loginRequest = null;
//        try {
//            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        UsernamePasswordAuthenticationToken authToken =
//                new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword());
//
//        Authentication authentication = authenticationManager.authenticate(authToken);
//
//        return authentication;
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//
//        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
//        Calendar calendar = Calendar.getInstance();
//
//        calendar.add(Calendar.SECOND, jwtProperties.getExpirationTime());
//        String accessToken = Jwts.builder()
//                .claim("userId", principalDetails.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(calendar.getTime())
//                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
//                .compact();
//
//        calendar.add(Calendar.SECOND, jwtProperties.getRefreshExpirationTime());
//        String refreshToken = Jwts.builder()
//                .setSubject(principalDetails.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(calendar.getTime())
//                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
//                .compact();
//
//        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken, jwtProperties.getTokenPrefix(), jwtProperties.getExpirationTime());
//        ObjectMapper objectMapper = new ObjectMapper();
//        String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tokenResponse);
//
//        System.out.println("Generated Access Token: " + accessToken);
//
//        PrintWriter printWriter = response.getWriter();
//        printWriter.write(result);
//        printWriter.close();
//    }
//
//}
