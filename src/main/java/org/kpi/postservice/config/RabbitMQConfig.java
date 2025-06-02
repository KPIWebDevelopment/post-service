package org.kpi.postservice.config;

import org.kpi.postservice.config.properties.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties properties;

    public RabbitMQConfig(RabbitMQProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DirectExchange imageExchange() {
        return new DirectExchange(properties.getExchange());
    }

    @Bean
    public Queue requestQueue() {
        return new Queue(properties.getRequestQueue(), true);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(properties.getResultQueue(), true);
    }

    @Bean
    public Binding requestBinding() {
        return BindingBuilder
                .bind(requestQueue())
                .to(imageExchange())
                .with(properties.getRequestRoutingKey());
    }

    @Bean
    public Binding resultBinding() {
        return BindingBuilder
                .bind(resultQueue())
                .to(imageExchange())
                .with(properties.getResultRoutingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
