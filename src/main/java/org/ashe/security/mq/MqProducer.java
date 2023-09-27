package org.ashe.security.mq;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        // Producer初始化为 confirm 模式，消息发送后会异步回调生产者
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                // 发送成功
                log.info("----> Message sent successfully.");
            } else {
                // 发送失败
                log.error("----> Failed to send message: " + cause);
            }
        });
    }


    public void sendMessage(String routingKey, Object message) {
        rabbitTemplate.convertAndSend(MqConf.DIRECT_EXCHANGE, routingKey, JSON.toJSON(message));
        log.info("----> Sent message: " + message);
    }
}
