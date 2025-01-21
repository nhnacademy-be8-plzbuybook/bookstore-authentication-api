package com.nhnacademy.shoppingmallservice.common.config;

import com.nhnacademy.shoppingmallservice.skm.properties.SKMProperties;
import com.nhnacademy.shoppingmallservice.skm.service.SecureKeyManagerService;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final SecureKeyManagerService secureKeyManagerService;
    private final SKMProperties skMProperties;

    public RedisConfig(SecureKeyManagerService secureKeyManagerService, SKMProperties skMProperties) {
        this.secureKeyManagerService = secureKeyManagerService;
        this.skMProperties = skMProperties;
    }

    RedisConnectionFactory createRedisConnectionFactory(String hostKey, String portKey, String passwordKey, String databaseKey) {
        String host = secureKeyManagerService.fetchSecret(hostKey);
        int port = Integer.parseInt(secureKeyManagerService.fetchSecret(portKey));
        String password = secureKeyManagerService.fetchSecret(passwordKey);
        int database = Integer.parseInt(secureKeyManagerService.fetchSecret(databaseKey));

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password);
        config.setDatabase(database);

        return new LettuceConnectionFactory(config);
    }


    @Bean(name = "jwtRedisConnectionFactory")
    public RedisConnectionFactory jwtRedisConnectionFactory() {
        return createRedisConnectionFactory(
                skMProperties.getRedis().getHost(),
                skMProperties.getRedis().getPort(),
                skMProperties.getRedis().getPassword(),
                skMProperties.getRedis().getRange()
        );
    }

    @Bean(name = "verifyCodeRedisConnectionFactory")
    public RedisConnectionFactory verifyCodeRedisConnectionFactory() {
        return createRedisConnectionFactory(
                skMProperties.getVerify_redis().getHost(),
                skMProperties.getVerify_redis().getPort(),
                skMProperties.getVerify_redis().getPassword(),
                skMProperties.getVerify_redis().getRange()
        );
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
