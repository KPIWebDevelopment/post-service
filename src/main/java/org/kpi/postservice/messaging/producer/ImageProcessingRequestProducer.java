package org.kpi.postservice.messaging.producer;

import lombok.RequiredArgsConstructor;
import org.kpi.postservice.config.properties.RabbitMQProperties;
import org.kpi.postservice.model.ImageProcessingRequestMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageProcessingRequestProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;

    public void sendImageProcessingRequestMessage(ImageProcessingRequestMessage message) {
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchange(), rabbitMQProperties.getRequestRoutingKey(), message);
    }
}
