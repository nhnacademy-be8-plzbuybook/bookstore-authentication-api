//package com.nhnacademy.shoppingmallservice.skm.test;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RedisConnectionTester {
//    private final RedisTemplate<String, Object> jwtRedisTemplate;
//    private final RedisTemplate<String, Object> verifyRedisTemplate;
//
//    public RedisConnectionTester(
//            @Qualifier("jwtRedisTemplate") RedisTemplate<String, Object> jwtRedisTemplate,
//            @Qualifier("verifyRedisTemplate") RedisTemplate<String, Object> verifyRedisTemplate) {
//        this.jwtRedisTemplate = jwtRedisTemplate;
//        this.verifyRedisTemplate = verifyRedisTemplate;
//    }
//
//    public void testRedisConnection() {
//        String jwtKey = "jwtTest11";
//        String jwtValue = "jwtValue11";
//        String verifyKey = "verifyTest11";
//        String verifyValue = "verifyValue11";
//
//        // JWT Redis 테스트
//        jwtRedisTemplate.opsForValue().set(jwtKey, jwtValue);
//        Object fetchedJwtValue = jwtRedisTemplate.opsForValue().get(jwtKey);
//        System.out.println("JWT Redis - Key: " + jwtKey + ", Value: " + fetchedJwtValue);
//
//        // Verify Redis 테스트
//        verifyRedisTemplate.opsForValue().set(verifyKey, verifyValue);
//        Object fetchedVerifyValue = verifyRedisTemplate.opsForValue().get(verifyKey);
//        System.out.println("Verify Redis - Key: " + verifyKey + ", Value: " + fetchedVerifyValue);
//    }
//}