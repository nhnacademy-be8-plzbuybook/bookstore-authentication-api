package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.InvalidTokenException;
import com.nhnacademy.shoppingmallservice.common.provider.JwtProvider;
import com.nhnacademy.shoppingmallservice.dto.MessagePayload;
import com.nhnacademy.shoppingmallservice.service.AccountService;
import com.nhnacademy.shoppingmallservice.webClient.DooraySendClient;
import com.nhnacademy.shoppingmallservice.webClient.MemberClient;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AccountServiceImpl implements AccountService {

    private final RedisTemplate<String, Object> verifyRedisTemplate;
    private final DooraySendClient dooraySendClient;
    private final MemberClient memberClient;
    private final JwtProvider jwtProvider;

    private static final String REDIS_KEY_PREFIX = "fronteend:auth:";

    public AccountServiceImpl(@Qualifier("verifyRedisTemplate") RedisTemplate<String, Object> verifyRedisTemplate, DooraySendClient dooraySendClient, MemberClient memberClient, JwtProvider jwtProvider) {
        this.verifyRedisTemplate = verifyRedisTemplate;
        this.dooraySendClient = dooraySendClient;
        this.memberClient = memberClient;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void sendVerificationCode(String userId) {
        String randomCode = randomCode();

        //유효 시간 5분
        verifyRedisTemplate.opsForValue().set(userId, randomCode, Duration.ofMinutes(5));

        MessagePayload messagePayload = new MessagePayload("회원 : " + userId, "인증 코드 : " + randomCode);
        dooraySendClient.sendMessage(messagePayload, 3204376758577275363L, 3970575113227416519L, "9HZMjIarQxSv_l1CHIp39g"); //책사조잉 단톡방
//        dooraySendClient.sendMessage(messagePayload, 3204376758577275363L, 3971180137082774187L, "1IWucOGgQ_WYeyTH-p62lQ"); //개인 톡방

//        https://nhnacademy.dooray.com/services/3204376758577275363/3970575113227416519/9HZMjIarQxSv_l1CHIp39g  책사조잉 단톡방
//        https://nhnacademy.dooray.com/services/3204376758577275363/3971180137082774187/1IWucOGgQ_WYeyTH-p62lQ 개인 톡방
    }

    @Override
    public boolean verifyCode(String token, String inputCode) {
        String redisKey = REDIS_KEY_PREFIX + token;
        Object userId = verifyRedisTemplate.opsForValue().get(redisKey);

        memberClient.activateMemberStatus((String) userId);

        if (userId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰!");
        }

        Object savedCode = verifyRedisTemplate.opsForValue().get(userId.toString());
        if (savedCode == null) {
            throw new IllegalArgumentException("인증 코드가 만료되었거나 존재하지 않다!");
        }

        return savedCode.toString().equals(inputCode);
    }

    private String randomCode(){
        //1000 ~ 9999
        return String.valueOf((int)(Math.random() * 9000) + 1000);
    }

    @Override
    public String getRoleFromToken(String token) {
        try{
            Claims claims = jwtProvider.parseToken(token);
            return claims.get("role", String.class);
        }catch (Exception e){
            throw new InvalidTokenException("Invalid JWT token", e);
        }
    }
}
