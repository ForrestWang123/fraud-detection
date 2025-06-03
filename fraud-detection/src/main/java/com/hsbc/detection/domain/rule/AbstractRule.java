package com.hsbc.detection.domain.rule;


import com.hsbc.detection.domain.FraudWarning;
import com.hsbc.detection.domain.entity.Transaction;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class AbstractRule {
    private final AbstractRule successor;

    public AbstractRule(AbstractRule successor) {
        this.successor = successor;
    }

    abstract void doDetect(Transaction transaction, List<FraudWarning> fraudWarnings);

    public void detect(Transaction transaction, List<FraudWarning> fraudWarnings) {
        doDetect(transaction, fraudWarnings);
        if (successor != null) {
            successor.detect(transaction, fraudWarnings);
        }
    }
}
