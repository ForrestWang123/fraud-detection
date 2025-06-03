package com.hsbc.detection.domain.rule;

import com.hsbc.detection.domain.FraudWarning;
import com.hsbc.detection.domain.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class AmountCheckRule extends AbstractRule {
    private final BigDecimal threshold;

    public AmountCheckRule(String amountCheckRuleThreshold,
                           SuspiciousAccountsRule successor) {
        super(successor);
        this.threshold = new BigDecimal(amountCheckRuleThreshold);
    }

    @Override
    void doDetect(Transaction transaction, List<FraudWarning> fraudWarnings) {
        if (transaction.getAmount().compareTo(threshold) > 0) {
            fraudWarnings.add(new FraudWarning(transaction.getTransactionId(), "The amount (" + transaction.getAmount() + ") exceeded the threshold of " + threshold));
        }
    }
}
