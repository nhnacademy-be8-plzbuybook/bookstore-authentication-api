package com.nhnacademy.shoppingmallservice.common.config;

import com.nhnacademy.shoppingmallservice.skm.properties.SKMProperties;
import com.nhnacademy.shoppingmallservice.skm.service.SecureKeyManagerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


// Refresh Token 을 저장하기 위한 Redis 설정
@Configuration
public class RedisConfig {

    private final SecureKeyManagerService secureKeyManagerService;
    private final SKMProperties skMProperties;

    public RedisConfig(SecureKeyManagerService secureKeyManagerService, SKMProperties skMProperties) {
        this.secureKeyManagerService = secureKeyManagerService;
        this.skMProperties = skMProperties;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> sessionRedisTemplate = new RedisTemplate<>();

        sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);
        sessionRedisTemplate.setKeySerializer(new StringRedisSerializer());
        sessionRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        sessionRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        sessionRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return sessionRedisTemplate;
    }

    @Bean
    public String getRedisHost() {
        return secureKeyManagerService.fetchSecret(skMProperties.getRedis().getHost());
    }

    @Bean
    public String getRedisPort(){
        return secureKeyManagerService.fetchSecret(skMProperties.getRedis().getPort());
    }

    @Bean
    public String getRedisPassword() {
        return secureKeyManagerService.fetchSecret(skMProperties.getRedis().getPassword());
    }

    @Bean
    public String getRedisRange(){
        return secureKeyManagerService.fetchSecret(skMProperties.getRedis().getRange());
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String host = secureKeyManagerService.fetchSecret(skMProperties.getRedis().getHost());
        int port = Integer.parseInt(secureKeyManagerService.fetchSecret(skMProperties.getRedis().getPort()));
        String password = secureKeyManagerService.fetchSecret(skMProperties.getRedis().getPassword());
        int database = Integer.parseInt(secureKeyManagerService.fetchSecret(skMProperties.getRedis().getRange()));

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setPassword(password);
        redisConfig.setDatabase(database);
        return new LettuceConnectionFactory(redisConfig);
    }
}
