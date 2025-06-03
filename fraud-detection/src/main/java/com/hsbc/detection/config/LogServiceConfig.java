package com.hsbc.detection.config;

import com.aliyun.openservices.aliyun.log.producer.LogProducer;
import com.aliyun.openservices.aliyun.log.producer.Producer;
import com.aliyun.openservices.aliyun.log.producer.ProducerConfig;
import com.aliyun.openservices.aliyun.log.producer.ProjectConfig;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class LogServiceConfig {
    @Value("${sls.accessKey}")
    private String accessKey;

    @Value("${sls.accessSecret}")
    private String accessSecret;

    @Value("${sls.endpoint}")
    private String slsEndpoint;

    @Value("${sls.projectName}")
    private String slsProjectName;

    @Value("${sls.logStoreName}")
    private String slsLogStoreName;

    @Value("${sls.producer.retries}")
    private int slsProducerRetries;

    @Value("${sls.producer.baseRetryBackoffMs}")
    private long slsProducerBaseRetryBackoffMs;

    @Value("${sls.producer.maxRetryBackoffMs}")
    private long slsProducerMaxRetryBackoffMs;

    @Value("${sls.producer.ioThreadCount}")
    private int slsProducerIoThreadCount;

    @Bean
    public Producer logClient() throws ClientException {
        ProjectConfig projectConfig = new ProjectConfig(
                slsProjectName,
                slsEndpoint,
                accessKey,
                accessSecret
        );
        ProducerConfig producerConfig = new ProducerConfig();
        producerConfig.setRetries(slsProducerRetries);
        producerConfig.setBaseRetryBackoffMs(slsProducerBaseRetryBackoffMs);
        producerConfig.setMaxRetryBackoffMs(slsProducerMaxRetryBackoffMs);
        producerConfig.setIoThreadCount(slsProducerIoThreadCount);
        LogProducer logProducer = new LogProducer(producerConfig);
        logProducer.putProjectConfig(projectConfig);
        return logProducer;
    }
}
