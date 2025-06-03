package com.hsbc.detection.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(builderClassName = "Builder")
@AllArgsConstructor
public class Transaction {
    @NonNull String transactionId;
    @NonNull String fromAccountId;
    @NonNull String toAccountId;
    @NonNull BigDecimal amount;
    long timestamp;
}
