package com.hsbc.detection.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class FraudWarning {
    private String message;
    private String transactionId;
}
