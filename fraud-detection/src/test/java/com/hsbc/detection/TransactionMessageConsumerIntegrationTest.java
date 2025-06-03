package com.hsbc.detection;

import com.hsbc.detection.BaseIntegrationTest;
import com.hsbc.detection.service.TransactionMessageConsumer;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@TestPropertySource(properties = {
    "rocketmq.endpoints=localhost:9876",
    "rocketmq.consumerGroup=fraud-detection-test-group"
})
class TransactionMessageConsumerIntegrationTest extends BaseIntegrationTest {

    @SpyBean
    private TransactionMessageConsumer transactionMessageConsumer;

    @SpyBean
    private SimpleConsumer simpleConsumer;

    @Autowired
    private Producer producer;

    private final Gson gson = new Gson();
    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @BeforeEach
    void setUp() {
        clearTestData();
        transactionMessageConsumer.start();
    }

//    @Test
//    void whenValidTransactionMessage_thenProcessedSuccessfully() throws Exception {
//        // Create a test transaction message
//        String testTransaction = "{\"transactionId\":\"test123\",\"amount\":1000.0,\"timestamp\":\"2024-03-20T10:00:00Z\"}";
//
//        // Create and send RocketMQ message
//        Message message = provider.newMessageBuilder()
//            .setTopic("transaction_topic")
//            .setTag("TAG")
//            .setBody(testTransaction.getBytes(StandardCharsets.UTF_8))
//            .build();
//
//        producer.send(message);
//
//        // Verify that the consumer received the message
//        verify(simpleConsumer, timeout(5000).times(1))
//            .receive(anyInt(), any(Duration.class));
//    }
//
//    @Test
//    void whenInvalidTransactionMessage_thenHandledGracefully() throws Exception {
//        // Create an invalid test transaction message
//        String invalidTransaction = "{\"invalid\":\"data\"}";
//
//        Message message = provider.newMessageBuilder()
//            .setTopic("transaction_topic")
//            .setTag("TAG")
//            .setBody(invalidTransaction.getBytes(StandardCharsets.UTF_8))
//            .build();
//
//        producer.send(message);
//
//        // Verify that the consumer received the message
//        verify(simpleConsumer, timeout(5000).times(1))
//            .receive(anyInt(), any(Duration.class));
//    }
} 