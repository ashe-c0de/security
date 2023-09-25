package org.ashe.security.mq;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ashe.security.config.MqConf;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String routingKey, Object message) {
        rabbitTemplate.convertAndSend(MqConf.DIRECT_EXCHANGE, routingKey, JSON.toJSON(message));
        log.info("----> Sent message: " + message);
    }
}
