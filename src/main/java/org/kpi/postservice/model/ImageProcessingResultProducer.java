package org.kpi.postservice.model;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageProcessingResultProducer {

    private final RabbitTemplate rabbitTemplate;
//    private final RabbitMQProperties rabbitMQProperties;

//    public void sendImageProcessingResultMessage(ImageProcessingResultMessage message) {
//        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchange(), rabbitMQProperties.getResultQueue(), message);
//    }
}
