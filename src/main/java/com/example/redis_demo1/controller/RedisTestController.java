package com.example.redis_demo1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
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

    @GetMapping("/verifyCode")
    public Map<String,String> verifyCode(String phone){
        //拼接key
        String countKey = "VerifyCode"+phone+":count";
        String codeKey = "VerifyCode"+phone+":code";
        String  message = "";
        Map<String,String> msgMap = new HashMap<>();
        //每个手机号每天只能发送三次
        Integer count = (Integer) redisTemplate.opsForValue().get(countKey);
        if(count == null){
            redisTemplate.opsForValue().set(countKey, 1, Duration.ofDays(1));
            message = "剩余次数为"+2;
        }else if(count<=2){
            redisTemplate.opsForValue().increment(countKey,1);
            message = "剩余次数为"+(2-count);
        }else if(count>2){
            msgMap.put("msg","剩余获取次数为0");
            return  msgMap;
        }
        String code = getCode();
        redisTemplate.opsForValue().set(codeKey,code, Duration.ofMinutes(2));
        redisTemplate.opsForValue().set("codeKey",codeKey,Duration.ofMinutes(2));
        String expire = redisTemplate.getExpire(codeKey).toString();
        msgMap.put("msg",message);
        msgMap.put("cod",code);
        msgMap.put("time","请于"+expire+"秒内填写验证码");
        return msgMap;
    }
    @GetMapping("/verify")
    public String verify(String code){
        String codeKey = (String)redisTemplate.opsForValue().get("codeKey");
        String realCode = (String)redisTemplate.opsForValue().get(codeKey);
        if(code.equals(realCode)){
            return "验证成功";
        }
        return "验证码不正确";
    }

    public String getCode(){
        Random random = new Random();
        String code = "";
        for (int i=0; i<6; i++){
            int rand = random.nextInt(10);
            code += rand;
        }
        return code;
    }
}
