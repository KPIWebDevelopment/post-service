package org.kpi.postservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "image-processing.rabbitmq")
public class RabbitMQProperties {
    private String exchange;
    private String requestQueue;
    private String requestRoutingKey;
    private String resultQueue;
    private String resultRoutingKey;
}
