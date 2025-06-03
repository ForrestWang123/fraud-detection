package com.hsbc.detection.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    @Test
    void whenValidTransaction_thenFieldsAreSetCorrectly() {
        // Arrange
        String transactionId = "TEST-123";
        String fromAccountId = "ACC-001";
        String toAccountId = "ACC-002";
        BigDecimal amount = new BigDecimal("500");
        long timestamp = System.currentTimeMillis();

        // Act
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .timestamp(timestamp)
                .build();

        // Assert
        assertThat(transaction.getTransactionId()).isEqualTo(transactionId);
        assertThat(transaction.getFromAccountId()).isEqualTo(fromAccountId);
        assertThat(transaction.getToAccountId()).isEqualTo(toAccountId);
        assertThat(transaction.getAmount()).isEqualTo(amount);
        assertThat(transaction.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void whenNullFields_thenThrowsException() {
        // Arrange
        String transactionId = null;
        String fromAccountId = "ACC-001";
        String toAccountId = "ACC-002";
        BigDecimal amount = new BigDecimal("500");
        long timestamp = System.currentTimeMillis();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> Transaction.builder()
                .transactionId(transactionId)
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .timestamp(timestamp)
                .build());
    }
}