package com.hsbc.detection;

import com.aliyun.openservices.aliyun.log.producer.Callback;
import com.aliyun.openservices.aliyun.log.producer.Producer;
import com.aliyun.openservices.log.common.LogItem;
import com.google.gson.Gson;
import com.hsbc.detection.domain.DetectProcessor;
import com.hsbc.detection.domain.entity.Transaction;
import com.hsbc.detection.service.TransactionMessageConsumer;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TransactionMessageConsumerIntegrationTest {

    @MockBean
    private SimpleConsumer simpleConsumer;

    @MockBean
    private Producer logProducer;

    @Autowired
    private TransactionMessageConsumer messageConsumer;

    @Autowired
    private DetectProcessor detectProcessor;

    private final Gson gson = new Gson();
    private final ClientServiceProvider provider = ClientServiceProvider.loadService();

    @Captor
    private ArgumentCaptor<List<LogItem>> logItemsCaptor;

    @Test
    void whenValidTransactionMessage_thenProcessedSuccessfully() throws Exception {
        // Create a valid transaction
        Transaction transaction = Transaction.builder()
                .transactionId("TEST-123")
                .fromAccountId("ACC-001")
                .toAccountId("ACC-002")
                .amount(new BigDecimal("500"))
                .timestamp(System.currentTimeMillis())
                .build();
        String transactionJson = gson.toJson(transaction);
        ByteBuffer messageBody = ByteBuffer.wrap(transactionJson.getBytes(StandardCharsets.UTF_8));

        // Mock the message view
        MessageView messageView = mock(MessageView.class);
        when(messageView.getBody()).thenReturn(messageBody);
        when(simpleConsumer.receive(anyInt(), any(Duration.class)))
            .thenAnswer(invocation -> Collections.singletonList(messageView));

        messageConsumer.pullMessages();

        // Verify that the message was processed
        verify(simpleConsumer, times(1)).ack(any(MessageView.class));
        // Verify that the log producer was called with fraud warning
        verify(logProducer, never()).send(
            anyString(),  // project
            anyString(),  // logstore
            any(List.class),  // logItems
            any(Callback.class)  // callback
        );
    }

    @Test
    void whenInValidTransactionMessage_thenProcessedSuccessfully() throws Exception {
        // Create a valid transaction
        String transactionJson = "{\"transactionId\":\"TEST-123\",\"fromAccountId\":\"ACC-001\",\"amount\":500,\"timestamp\":1748981376510}";
        ByteBuffer messageBody = ByteBuffer.wrap(transactionJson.getBytes(StandardCharsets.UTF_8));

        // Mock the message view
        MessageView messageView = mock(MessageView.class);
        when(messageView.getBody()).thenReturn(messageBody);
        when(simpleConsumer.receive(anyInt(), any(Duration.class)))
                .thenAnswer(invocation -> Collections.singletonList(messageView));
        messageConsumer.pullMessages();
        // Verify that the message was processed
        verify(simpleConsumer, times(1)).ack(any(MessageView.class));
        // Verify that the log producer was called with fraud warning
        verify(logProducer, never()).send(
                anyString(),  // project
                anyString(),  // logstore
                any(List.class),  // logItems
                any(Callback.class)  // callback
        );
    }

    @Test
    void whenValidFraudTransactionMessage() throws Exception {
        // Create a valid transaction
        Transaction transaction = Transaction.builder()
                .transactionId("TEST-123")
                .fromAccountId("ACC-001")
                .toAccountId("ACC-002")
                .amount(new BigDecimal("2000"))
                .timestamp(System.currentTimeMillis())
                .build();
        String transactionJson = gson.toJson(transaction);
        ByteBuffer messageBody = ByteBuffer.wrap(transactionJson.getBytes(StandardCharsets.UTF_8));

        // Mock the message view
        MessageView messageView = mock(MessageView.class);
        when(messageView.getBody()).thenReturn(messageBody);
        when(simpleConsumer.receive(anyInt(), any(Duration.class)))
                .thenAnswer(invocation -> Collections.singletonList(messageView));

        messageConsumer.pullMessages();

        // Verify that the message was processed
        // Verify that the log producer was called with fraud warning
        verify(logProducer, times(1)).send(
                anyString(),  // project
                anyString(),  // logstore
                any(List.class),  // logItems
                any(Callback.class)  // callback
        );

        // Verify the content of the log items
        verify(logProducer).send(
            anyString(),
            anyString(),
            logItemsCaptor.capture(),
            any(Callback.class)
        );
        List<LogItem> logItems = logItemsCaptor.getValue();
        assert !logItems.isEmpty();
    }
} 