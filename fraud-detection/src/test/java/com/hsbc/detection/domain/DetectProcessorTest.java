package com.hsbc.detection.domain;

import com.hsbc.detection.domain.entity.Transaction;
import com.hsbc.detection.domain.rule.AmountCheckRule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class DetectProcessorTest {

    private DetectProcessor detectProcessor;

    @Mock
    private LogFraudNotifier fraudNotifier;

    @Mock
    private FinishedCallBack callBack;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        MockitoAnnotations.openMocks(this);
        detectProcessor = new DetectProcessor();

        AmountCheckRule amountCheckRule = new AmountCheckRule("1000", null);
        Field amountField = DetectProcessor.class.getDeclaredField("rootRule");
        amountField.setAccessible(true);
        amountField.set(detectProcessor, amountCheckRule);

        Field notifierField = DetectProcessor.class.getDeclaredField("fraudNotifier");
        notifierField.setAccessible(true);
        notifierField.set(detectProcessor, fraudNotifier);
    }

    @Test
    void testDetect_NoFraudWarnings() throws FinishedCallBack.FinishedCallBackException {
        Transaction transaction = Transaction.builder()
                .transactionId("TX123456")
                .fromAccountId("ACC1001")
                .toAccountId("ACC2002")
                .amount(new BigDecimal("999.99"))
                .timestamp(System.currentTimeMillis())
                .build();
        doAnswer(invocation -> null).when(fraudNotifier).notify(anyList(), any(FinishedCallBack.class));

        detectProcessor.detect(transaction, callBack);
        verify(callBack, times(1)).call();
        verify(fraudNotifier, never()).notify(anyList(), eq(callBack));
    }

    @Test
    void testDetect_WithFraudWarnings() throws FinishedCallBack.FinishedCallBackException {
        Transaction transaction = Transaction.builder()
                .transactionId("TX123456")
                .fromAccountId("ACC1001")
                .toAccountId("ACC2002")
                .amount(new BigDecimal("1001"))
                .timestamp(System.currentTimeMillis())
                .build();
        doAnswer(invocation -> null).when(fraudNotifier).notify(anyList(), any(FinishedCallBack.class));
        detectProcessor.detect(transaction, callBack);
        verify(callBack, never()).call();
        verify(fraudNotifier, times(1)).notify(anyList(), eq(callBack));
    }
}