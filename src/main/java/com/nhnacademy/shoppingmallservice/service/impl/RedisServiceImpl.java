package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.NotFoundException;
import com.nhnacademy.shoppingmallservice.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveValueOnRedis(String key, Object value, long expiry) {
        if (key.isBlank() || value == null) {
            throw new IllegalArgumentException("invalid parameter for saving Redis!");
        }
        if (value instanceof String && ((String) value).isBlank()) {
            throw new IllegalArgumentException("invalid parameter for saving Redis!");
        }
        redisTemplate.opsForValue().set(key, value, expiry, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object getValueFromRedis(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }

        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return value;
        }
        throw new NotFoundException("resource not found on Redis!");
    }
}
