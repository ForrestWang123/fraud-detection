package com.hsbc.detection.domain.rule;

import com.hsbc.detection.domain.FraudWarning;
import com.hsbc.detection.domain.entity.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmountCheckRuleTest {
    @Test
    void testDoDetectNormal() {
        AmountCheckRule rule = new AmountCheckRule("500", null);

        Transaction transactionBelowThreshold = new Transaction("txn1", "12345", "54321", new BigDecimal("400"), 300);
        Transaction transactionAboveThreshold = new Transaction("txn2", "54321", "67890", new BigDecimal("600"), 300);

        List<FraudWarning> fraudWarnings = new ArrayList<>();

        rule.doDetect(transactionBelowThreshold, fraudWarnings);
        rule.doDetect(transactionAboveThreshold, fraudWarnings);

        assertEquals(1, fraudWarnings.size());
        assertEquals(new FraudWarning("txn2", "The amount (600) exceeded the threshold of 500"), fraudWarnings.get(0));
    }

    @Test
    void testDoDetectBoundaryScenario() {
        AmountCheckRule rule = new AmountCheckRule("500", null);

        Transaction transactionEqualThreshold = new Transaction("txn1", "12345", "54321", new BigDecimal("500"), 300);
        Transaction transactionNegativeAmount = new Transaction("txn2", "54321", "67890", new BigDecimal("-100"), 300);
        Transaction transactionZeroAmount = new Transaction("txn3", "54321", "98765", new BigDecimal("0"), 300);

        List<FraudWarning> fraudWarnings = new ArrayList<>();

        rule.doDetect(transactionEqualThreshold, fraudWarnings);
        rule.doDetect(transactionNegativeAmount, fraudWarnings);
        rule.doDetect(transactionZeroAmount, fraudWarnings);

        assertEquals(0, fraudWarnings.size());
    }
}