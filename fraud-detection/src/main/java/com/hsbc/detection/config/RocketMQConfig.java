package com.hsbc.detection.config;

import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.consumer.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class RocketMQConfig {
    @Value("${rocketmq.accessKey}")
    private String accessKey;

    @Value("${rocketmq.accessSecret}")
    private String accessSecret;

    @Value("${rocketmq.endpoints}")
    private String rocketMqEndpoints;

    @Value("${rocketmq.consumerGroup}")
    private String consumerGroup;

    @Value("${rocketmq.topic}")
    private String topic;

    @Value("${rocketmq.awaitDurationSeconds}")
    private long awaitDurationSeconds;

    @Bean
    public SimpleConsumer simpleConsumer() throws ClientException {
        StaticSessionCredentialsProvider staticSessionCredentialsProvider = new StaticSessionCredentialsProvider(accessKey, accessSecret);
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
            .setEndpoints(rocketMqEndpoints)
            .setCredentialProvider(staticSessionCredentialsProvider)
            .build();
        return ClientServiceProvider.loadService().newSimpleConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                .setConsumerGroup(consumerGroup)
                .setAwaitDuration(Duration.ofSeconds(awaitDurationSeconds))
                .setSubscriptionExpressions(Collections.singletonMap(topic, new FilterExpression("*", FilterExpressionType.TAG)))
                .build();
    }
}
