package com.hsbc.detection.domain.rule;

import com.hsbc.detection.domain.FraudWarning;
import com.hsbc.detection.domain.entity.Transaction;

import java.util.List;

public class SuspiciousAccountsRule extends AbstractRule {
    private final List<String> suspiciousAccounts;

    public SuspiciousAccountsRule(String suspiciousAccounts, AbstractRule successor) {
        super(successor);
        this.suspiciousAccounts = List.of(suspiciousAccounts.split(","));
    }

    @Override
    void doDetect(Transaction transaction, List<FraudWarning> fraudWarnings) {
        if (suspiciousAccounts.contains(transaction.getFromAccountId())) {
            fraudWarnings.add(new FraudWarning(transaction.getTransactionId(), "from a suspicious account:" + transaction.getFromAccountId()));
        }

        if (suspiciousAccounts.contains(transaction.getToAccountId())) {
            fraudWarnings.add(new FraudWarning(transaction.getTransactionId(), "to a suspicious account:" + transaction.getToAccountId()));
        }
    }
}
