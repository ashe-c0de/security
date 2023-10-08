package org.ashe.security.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.ashe.security.config.MqConf;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Slf4j
public class MqConsumer {

    @RabbitListener(queues = MqConf.HELLO_QUEUE, ackMode = "MANUAL")
    public void handleMessage(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("<---- Received message: " + new String(message.getBody()));
            log.info("deliveryTag: {}", deliveryTag);
            // 手动确认签收
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 拒绝签收，消息重新入队
            channel.basicReject(deliveryTag, true);
        }
    }
}
