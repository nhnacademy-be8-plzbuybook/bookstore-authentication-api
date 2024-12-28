//package com.nhnacademy.shoppingmallservice.skm.test;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class RedisConnectionTester {
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public void testRedisConnection() {
//        try {
//            redisTemplate.opsForValue().set("testKey", "testValue");
//            String value = (String) redisTemplate.opsForValue().get("testKey");
//            System.out.println("Redis 연결 성공: " + value);
//        } catch (Exception e) {
//            System.err.println("Redis 연결 실패: " + e.getMessage());
//        }
//    }
//}
