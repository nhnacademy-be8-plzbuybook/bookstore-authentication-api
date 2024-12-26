package com.nhnacademy.shoppingmallservice.service.impl;

import com.nhnacademy.shoppingmallservice.common.exception.NotFoundException;
import com.nhnacademy.shoppingmallservice.service.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceImplTest {
    @Mock
    private RedisTemplate<String, Object> mockRedisTemplate;
    @InjectMocks
    private RedisServiceImpl redisService;

    @Test
    void saveValueOnRedis() {
        //given
        String key = "key";
        Object value = "value";
        Long expiry = 100000L;

        ValueOperations<String, Object> mockValueOperation = mock(ValueOperations.class);
        when(mockRedisTemplate.opsForValue()).thenReturn(mockValueOperation);
        doNothing().when(mockValueOperation).set(key, value, expiry, TimeUnit.MILLISECONDS);

        //when
        redisService.saveValueOnRedis(key, value, expiry);

        //then
        verify(mockValueOperation).set(
                eq(key),
                eq(value),
                eq(expiry),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void saveValueOnRedis_invalid_parameter() {
        //given
        String key = "key";
        Object value = " ";
        Long expiry = 100000L;

        //when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> redisService.saveValueOnRedis(key, value, expiry));

        //then
        assertEquals("invalid parameter for saving Redis!", e.getMessage());
    }

    @Test
    void getValueFromRedis() {
        String key = "key";
        Object value = "value";
        ValueOperations<String, Object> mockValueOperation = mock(ValueOperations.class);
        when(mockRedisTemplate.opsForValue()).thenReturn(mockValueOperation);
        when(mockValueOperation.get(key)).thenReturn(value);

        //when
        Object valueFromRedis = redisService.getValueFromRedis(key);

        //then
        assertEquals(value, valueFromRedis);
        verify(mockValueOperation).get(key);
    }

    @Test
    void getValueFromRedis_not_found() {
        String key = "key";
        ValueOperations<String, Object> mockValueOperation = mock(ValueOperations.class);
        when(mockRedisTemplate.opsForValue()).thenReturn(mockValueOperation);
        when(mockValueOperation.get(key)).thenReturn(null);

        //when
        NotFoundException e = assertThrows(NotFoundException.class, () -> redisService.getValueFromRedis(key));

        //then
        assertEquals("resource not found on Redis!", e.getMessage());
        verify(mockValueOperation).get(key);
    }

    @Test
    void getValueFromRedis_null_parameter() {
        ValueOperations<String, Object> mockValueOperation = mock(ValueOperations.class);

        //when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> redisService.getValueFromRedis(null));

        //then
        assertEquals("key can not be null", e.getMessage());
        verify(mockValueOperation, times(0)).get(anyString());
    }
}