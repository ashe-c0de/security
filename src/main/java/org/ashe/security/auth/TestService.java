package org.ashe.security.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ashe.security.config.MqConf;
import org.ashe.security.domain.RedisKey;
import org.ashe.security.infra.EmergencyException;
import org.ashe.security.mq.MqProducer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {

    private final StringRedisTemplate stringRedisTemplate;
    private final MqProducer mqProducer;

    public String hello() {
        String redisKey = RedisKey.getKey("count", "people");
        Long count = stringRedisTemplate.opsForValue().increment(redisKey);
        stringRedisTemplate.expire(redisKey,1, TimeUnit.DAYS);
        log.info("----> welcome the {} victim <----", count);
        return String.format("----> welcome the %s victim <----", count);
    }

    public void dingTalk() {
        try {
            int a = 1;
            int b = 0;
            int c = a / b;
            log.info("c = {}", c);
        } catch (Exception e) {
            throw new EmergencyException(e, "15283871282", "xx业务xx功能发生异常");
        }
    }

    public void mq() {
        mqProducer.sendMessage(MqConf.HELLO_KEY, "hey RabbitMQ");
    }

    public void aspectException(){
        throw new RuntimeException("welcome to aspectException");
    }
}
