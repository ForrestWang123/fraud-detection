package com.hsbc.detection.domain.rule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RuleFactory {
    @Value("${fraudDetection.amountCheckRuleThreshold}")
    String amountCheckRuleThreshold;

    @Value("${fraudDetection.suspiciousAccounts}")
    String suspiciousAccounts;

    private AbstractRule rootRule;

    public AbstractRule getRootRule() {
        if (rootRule == null) {
            rootRule = createRootRule();
        }

        return rootRule;
    }

    private AbstractRule createRootRule() {
        return new AmountCheckRule(amountCheckRuleThreshold, new SuspiciousAccountsRule(suspiciousAccounts, null));
    }
}
