package com.nhnacademy.shoppingmallservice.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


// Refresh Token 을 저장하기 위한 Redis 설정
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.port}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;


    @Bean(name = "jwtRedisConnectionFactory")
    public RedisConnectionFactory jwtRedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setPassword(password);
        redisConfig.setDatabase(231);
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean(name = "verifyCodeRedisConnectionFactory")
    public RedisConnectionFactory verifyCodeRedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setPassword(password);
        redisConfig.setDatabase(234);
        return new LettuceConnectionFactory(redisConfig);
    }

    RedisTemplate<String, Object> createRedisTemplate(
            RedisConnectionFactory connectionFactory,
            boolean isValueJson
    ) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        if (isValueJson) {
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        } else {
            redisTemplate.setValueSerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        }

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Primary
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> defaultRedisTemplate(@Qualifier("jwtRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, true);
    }

    @Bean(name = "verifyRedisTemplate")
    public RedisTemplate<String, Object> verifyRedisTemplate(RedisConnectionFactory verifyCodeRedisConnectionFactory) {
        return createRedisTemplate(verifyCodeRedisConnectionFactory, true);
    }

    @Bean(name = "jwtRedisTemplate")
    public RedisTemplate<String, Object> jwtTokenRedisTemplate(RedisConnectionFactory jwtRedisConnectionFactory) {
        return createRedisTemplate(jwtRedisConnectionFactory, false);
    }
}
