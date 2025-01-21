package com.nhnacademy.shoppingmallservice.common.config;

import com.nhnacademy.shoppingmallservice.skm.properties.SKMProperties;
import com.nhnacademy.shoppingmallservice.skm.service.SecureKeyManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisConfigTest {

    @Mock
    private SecureKeyManagerService secureKeyManagerService;

    @Mock
    private SKMProperties skMProperties;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @InjectMocks
    private RedisConfig redisConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock SecureKeyManagerService
        when(secureKeyManagerService.fetchSecret("redis.host")).thenReturn("localhost");
        when(secureKeyManagerService.fetchSecret("redis.port")).thenReturn("6379");
        when(secureKeyManagerService.fetchSecret("redis.password")).thenReturn("password");
        when(secureKeyManagerService.fetchSecret("redis.range")).thenReturn("0");

        when(secureKeyManagerService.fetchSecret("verify_redis.host")).thenReturn("localhost");
        when(secureKeyManagerService.fetchSecret("verify_redis.port")).thenReturn("6380");
        when(secureKeyManagerService.fetchSecret("verify_redis.password")).thenReturn("verifyPassword");
        when(secureKeyManagerService.fetchSecret("verify_redis.range")).thenReturn("1");

        // Mock SKMProperties with Redis and VerifyRedis inner classes
        SKMProperties.Redis redisConfig = new SKMProperties.Redis();
        redisConfig.setHost("localhost");
        redisConfig.setPort("6379");
        redisConfig.setPassword("password");
        redisConfig.setRange("0");

        SKMProperties.VerifyRedis verifyRedisConfig = new SKMProperties.VerifyRedis();
        verifyRedisConfig.setHost("localhost");
        verifyRedisConfig.setPort("6380");
        verifyRedisConfig.setPassword("verifyPassword");
        verifyRedisConfig.setRange("1");

        when(skMProperties.getRedis()).thenReturn(redisConfig);
        when(skMProperties.getVerify_redis()).thenReturn(verifyRedisConfig);
    }

    @Test
    void testCreateRedisConnectionFactory() {
        // When
        RedisConnectionFactory connectionFactory = redisConfig.createRedisConnectionFactory(
                "redis.host", "redis.port", "redis.password", "redis.range");

        // Then
        assertNotNull(connectionFactory);
        assertTrue(connectionFactory instanceof LettuceConnectionFactory);
    }


    @Test
    void testCreateRedisTemplate() {
        // When
        RedisTemplate<String, Object> redisTemplate = redisConfig.createRedisTemplate(redisConnectionFactory, true);

        // Then
        assertNotNull(redisTemplate);
        assertTrue(redisTemplate.getKeySerializer() instanceof StringRedisSerializer);
        assertTrue(redisTemplate.getValueSerializer() instanceof GenericJackson2JsonRedisSerializer);
    }

    @Test
    void testDefaultRedisTemplate() {
        // When
        RedisTemplate<String, Object> redisTemplate = redisConfig.defaultRedisTemplate(redisConnectionFactory);

        // Then
        assertNotNull(redisTemplate);
        assertTrue(redisTemplate.getKeySerializer() instanceof StringRedisSerializer);
        assertTrue(redisTemplate.getValueSerializer() instanceof GenericJackson2JsonRedisSerializer);
    }

    @Test
    void testVerifyRedisTemplate() {
        // When
        RedisTemplate<String, Object> redisTemplate = redisConfig.verifyRedisTemplate(redisConnectionFactory);

        // Then
        assertNotNull(redisTemplate);
        assertTrue(redisTemplate.getKeySerializer() instanceof StringRedisSerializer);
        assertTrue(redisTemplate.getValueSerializer() instanceof GenericJackson2JsonRedisSerializer);
    }

    @Test
    void testJwtTokenRedisTemplate() {
        // When
        RedisTemplate<String, Object> redisTemplate = redisConfig.jwtTokenRedisTemplate(redisConnectionFactory);

        // Then
        assertNotNull(redisTemplate);
        assertTrue(redisTemplate.getKeySerializer() instanceof StringRedisSerializer);
        assertTrue(redisTemplate.getValueSerializer() instanceof StringRedisSerializer);
    }
}