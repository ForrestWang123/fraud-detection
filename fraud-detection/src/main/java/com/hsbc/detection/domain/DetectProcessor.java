package com.hsbc.detection.domain;

import com.hsbc.detection.domain.entity.Transaction;
import com.hsbc.detection.domain.rule.AbstractRule;
import com.hsbc.detection.domain.rule.RuleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DetectProcessor {
    @Autowired
    private AbstractRule rootRule;
    @Autowired
    private LogFraudNotifier fraudNotifier;
    @Autowired
    private RuleFactory ruleFactory;

    public DetectProcessor() {
    }

    @PostConstruct
    public void init() {
        rootRule = ruleFactory.getRootRule();
    }

    public void detect(Transaction transaction, FinishedCallBack callBack) throws FinishedCallBack.FinishedCallBackException {
        List<FraudWarning> fraudWarnings = new ArrayList<>();
        rootRule.detect(transaction, fraudWarnings);

        if (fraudWarnings.isEmpty()) {
            callBack.call();
            if (log.isDebugEnabled()) {
                log.debug("Transaction {} passed all fraud checks.", transaction.getTransactionId());
            }
        } else {
            fraudNotifier.notify(fraudWarnings, callBack);
        }
    }

}
