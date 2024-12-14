package com.nhnacademy.shoppingmallservice.service;

public interface RedisService {
    void saveValueOnRedis(String key, Object value, long expiry);
    Object getValueFromRedis(String key);
}
