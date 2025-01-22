package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.NotFoundException;
import com.nhnacademy.shoppingmallservice.service.RedisService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
//TODO 레디스 추가로 주입 방법 수정 되어서 제가 임의로 주입 했습니다 혹시 레디스 관련 문제 생기면 말해주세요~ - 지호 -
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisServiceImpl(@Qualifier("jwtRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

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
