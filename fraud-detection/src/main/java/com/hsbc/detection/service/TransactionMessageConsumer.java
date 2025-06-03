package com.hsbc.detection.service;

import com.google.gson.Gson;
import com.hsbc.detection.domain.DetectProcessor;
import com.hsbc.detection.domain.FinishedCallBack;
import com.hsbc.detection.domain.entity.Transaction;
import com.hsbc.detection.util.DetectionThreadFactory;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TransactionMessageConsumer {
    private static final Logger log = LoggerFactory.getLogger(TransactionMessageConsumer.class);
    private final AtomicBoolean running = new AtomicBoolean(true);
    @Autowired
    private SimpleConsumer simpleConsumer;
    @Value("${rocketmq.maxMessageNum:100}")
    private int maxMessageNum;
    @Value("${rocketmq.invisibleDurationSeconds:30}")
    private long invisibleDurationSeconds;
    private final ThreadPoolExecutor threadPoolExecutor; // CPU-Intensive, so the max thread num is set to availableProcessors * 2
    @Autowired
    private DetectProcessor detectProcessor;
    private static final Gson gson = new Gson();

    public TransactionMessageConsumer(@Value("${fraudDetection.messageConsumerThreadPoolQueueSize}") int blockingQueueSize,
                                      @Value("${fraudDetection.messageConsumerCorePoolSize}") int corePoolSize,
                                      @Value("${fraudDetection.messageConsumerMaxPoolSize}") int maxPoolSize,
                                      @Value("${fraudDetection.messageConsumerKeepAliveTimeSeconds}") long keepAliveTime) {
        LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(blockingQueueSize);
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                blockingQueue,
                new DetectionThreadFactory("TransactionMessageConsumer-"),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public void start() {
        int processorsNum = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < processorsNum; i++) {
            threadPoolExecutor.submit(() -> {
                while (running.get()) {
                    try {
                        List<MessageView> messages = simpleConsumer.receive(maxMessageNum, Duration.ofSeconds(invisibleDurationSeconds));
                        if (messages.isEmpty()) {
                            continue;
                        }
                        for (MessageView message : messages) {
                            String msgBodyString = StandardCharsets.UTF_8.decode(message.getBody()).toString();
                            try {
                                Transaction transaction = gson.fromJson(msgBodyString, Transaction.class);
                                detectProcessor.detect(transaction, () -> {
                                    try {
                                        simpleConsumer.ack(message);
                                    } catch (ClientException e) {
                                        throw new FinishedCallBack.FinishedCallBackException("ack failed", e);
                                    }
                                });
                            } catch (FinishedCallBack.FinishedCallBackException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    } catch (Throwable t) {
                        if (running.get()) {
                            log.error("Error consuming, message:" + t.getMessage(), t);
                        }
                    }
                }
            });
        }
    }

    public void shutdown() {
        running.set(false);

        if (simpleConsumer != null) {
            try {
                simpleConsumer.close();
                System.out.println("RocketMQ consumer closed.");
            } catch (Exception e) {
                System.err.println("Error closing consumer: " + e.getMessage());
            }
        }
    }
}