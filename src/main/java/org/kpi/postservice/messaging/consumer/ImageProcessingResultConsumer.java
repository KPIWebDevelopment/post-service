package org.kpi.postservice.messaging.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.kpi.postservice.model.ImageProcessingResult;
import org.kpi.postservice.model.ImageProcessingResultMessage;
import org.kpi.postservice.service.PostService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ImageProcessingResultConsumer {

    private final PostService postService;

    @RabbitListener(
            queues = "${image-processing.rabbitmq.request-queue}",
            messageConverter = "jackson2JsonMessageConverter"
    )
    public void handleImageProcessingRequestMessage(
            ImageProcessingResultMessage message,
            Channel channel,
            Message amqpMessage
    ) throws IOException {
        var deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();
        var post = postService.getPostById(message.postId());
        if (message.imageProcessingResult().equals(ImageProcessingResult.SUCCESS)) {
            post.setImageSaved(true);
            postService.update(post.getId(), post);
        } else {
            postService.delete(post);
        }
        channel.basicAck(deliveryTag, false);
    }
}
