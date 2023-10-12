package com.help.loan.distribute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class TestRedisServiceImpl implements TestRedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void test(String key, String value) {

        ValueOperations opt = stringRedisTemplate.opsForValue();
        boolean isSet = opt.setIfAbsent(key, value);
        System.out.println("------------->" + isSet);
    }
}
