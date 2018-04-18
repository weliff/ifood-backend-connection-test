package br.com.ifood.ifoodconnection.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@Profile("!test")
@Configuration
public class AmqpConfig {

    private static final String EXCHANGE_TYPE = "x-delayed-message";
    private static final boolean EXCHANGE_DURABLE = true;
    private static final boolean EXCHANGE_AUTO_DELETE = false;
    private static final Map<String, Object> EXCHANGE_ARGUMENTS = Map.of("x-delayed-type", "direct");

    @Bean
    public InitializingBean startConfig(AmqpAdmin amqpAdmin) {
        return () -> {
            declareCustomExchange(amqpAdmin, Exchanges.RESTAURANT_STATUS_CHANGE);
            setupQueue(amqpAdmin, Exchanges.RESTAURANT_STATUS_CHANGE, Queues.RESTAURANT_STATUS_CHANGE);
        };
    }

    private void declareCustomExchange(AmqpAdmin amqpAdmin, String exchangeName) {
        CustomExchange exchange = buildCustomExchange(exchangeName);
        amqpAdmin.declareExchange(exchange);
    }

    private CustomExchange buildCustomExchange(String exchangeName) {
        return new CustomExchange(exchangeName, EXCHANGE_TYPE, EXCHANGE_DURABLE, EXCHANGE_AUTO_DELETE, EXCHANGE_ARGUMENTS);
    }

    private void setupQueue(AmqpAdmin amqpAdmin, String exchangeName, String queueName) {
        setupQueue(amqpAdmin, exchangeName, queueName, queueName);
    }

    private void setupQueue(AmqpAdmin amqpAdmin, String exchangeName, String queueName, String routingKey) {
        Queue queue = new Queue(queueName, true);
        CustomExchange exchange = buildCustomExchange(exchangeName);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();

        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
    }
}