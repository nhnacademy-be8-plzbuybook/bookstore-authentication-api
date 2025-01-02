//package com.nhnacademy.shoppingmallservice.skm.test;
//
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RedisConnectionTestRunner {
//    private final RedisConnectionTester redisConnectionTester;
//
//    public RedisConnectionTestRunner(RedisConnectionTester redisConnectionTester) {
//        this.redisConnectionTester = redisConnectionTester;
//    }
//
//    @Bean
//    public ApplicationRunner runner() {
//        return args -> redisConnectionTester.testRedisConnection();
//    }
//}
