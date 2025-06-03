package com.hsbc.detection;

import com.hsbc.detection.service.TransactionMessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class FraudDetectionApplication implements CommandLineRunner {
    @Autowired
    private TransactionMessageConsumer rocketMQConsumerService;

    public static void main(String[] args) {
        SpringApplication.run(FraudDetectionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        rocketMQConsumerService.start();
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("Graceful shutdown initiated...");
        rocketMQConsumerService.shutdown();
    }
}
