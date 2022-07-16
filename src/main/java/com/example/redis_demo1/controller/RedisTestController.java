package com.example.redis_demo1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Random;

@RestController
public class RedisTestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/testRedis")
    public String testRedis(){
        redisTemplate.opsForValue().set("name","张三");
        return redisTemplate.opsForValue().get("name").toString();
    }

    @GetMapping("/getCode")
    public String getCode(String phone){
        Random random = new Random();
        String code = "";
        for (int i=0; i<6; i++){
            int rand = random.nextInt(10);
            code += rand;
        }
        verifyCode(phone,code);
        String codeKey = "VerifyCode"+phone+":code";
        redisTemplate.opsForValue().set(codeKey,code,120);
        return code;
    }
    public void verifyCode(String phone, String code){
        //拼接key
        String countKey = "VerifyCode"+phone+":count";


        //每个手机号每天只能发送三次
        String count = (String) redisTemplate.opsForValue().get(countKey);
        if(count == null){
            redisTemplate.opsForValue().set("1", countKey, 24*60*60);
        }else if(Integer.parseInt(count)<=2){
            redisTemplate.opsForValue().increment(countKey);
        }else if(Integer.parseInt(count)>2){
        }

    }
}
