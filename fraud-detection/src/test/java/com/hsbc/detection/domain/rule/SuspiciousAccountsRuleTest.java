package com.hsbc.detection.domain.rule;

import com.hsbc.detection.domain.FraudWarning;
import com.hsbc.detection.domain.entity.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SuspiciousAccountsRuleTest {
    @Test
    void testDoDetect() {
        String[] suspiciousAccounts = {};
        SuspiciousAccountsRule rule = new SuspiciousAccountsRule("12345,67890", null);

        Transaction transactionFromSuspiciousAccount = new Transaction("txn1", "12345", "54321", new BigDecimal("1000"), 300);
        Transaction transactionToSuspiciousAccount = new Transaction("txn2", "54321", "67890", new BigDecimal("1000"), 300);
        Transaction transactionNotSuspicious = new Transaction("txn3", "54321", "98765", new BigDecimal("1000"), 300);

        List<FraudWarning> fraudWarnings = new ArrayList<>();

        // Act
        rule.doDetect(transactionFromSuspiciousAccount, fraudWarnings);
        rule.doDetect(transactionToSuspiciousAccount, fraudWarnings);
        rule.doDetect(transactionNotSuspicious, fraudWarnings);

        // Assert
        assertEquals(2, fraudWarnings.size());
        assertEquals(new FraudWarning("txn1", "from a suspicious account:12345"), fraudWarnings.get(0));
        assertEquals(new FraudWarning("txn2", "to a suspicious account:67890"), fraudWarnings.get(1));

        Transaction transactionBothSuspiciousAccount = new Transaction("txn3", "12345", "67890", new BigDecimal("1000"), 300);
        fraudWarnings.clear();
        rule.doDetect(transactionBothSuspiciousAccount, fraudWarnings);
        assertEquals(2, fraudWarnings.size());
        assertEquals(new FraudWarning("txn3", "from a suspicious account:12345"), fraudWarnings.get(0));
        assertEquals(new FraudWarning("txn3", "to a suspicious account:67890"), fraudWarnings.get(1));
    }

    @Test
    void testBoundaryCases() {
        // Case 1: Empty suspiciousAccounts array
        SuspiciousAccountsRule ruleWithEmptyAccounts = new SuspiciousAccountsRule("", null);

        Transaction transaction = new Transaction("txn1", "12345", "67890", new BigDecimal("1000"), 300);
        List<FraudWarning> fraudWarnings = new ArrayList<>();
        ruleWithEmptyAccounts.doDetect(transaction, fraudWarnings);
        assertEquals(0, fraudWarnings.size());
    }
}
