package org.ashe.security.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConf {

    /**
     * routingKey
     */
    public static final String HELLO_KEY = "hello_key";

    /**
     * queue name
     */
    public static final String HELLO_QUEUE = "hello_queue";

    /**
     * exchange
     */
    public static final String DIRECT_EXCHANGE = "direct_exchange";

    /**
     * hello queue
     */
    @Bean
    public Queue helloQueue() {
        return QueueBuilder.durable(HELLO_QUEUE).build();
    }

    @Bean
    public DirectExchange directExchange(){
        return ExchangeBuilder.directExchange(DIRECT_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * hello queue binding
     */
    @Bean
    public Binding helloQueueBinding() {
        return BindingBuilder
                .bind(helloQueue())
                .to(directExchange())
                .with(HELLO_KEY);
    }

}
