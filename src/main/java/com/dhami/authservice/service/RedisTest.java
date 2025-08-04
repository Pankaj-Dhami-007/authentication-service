package com.dhami.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    public void testSendEmail(){

        redisTemplate.opsForValue().set("email", "gmail@email.com");
        Object email = redisTemplate.opsForValue().get("email");
    }
}
