package com.hsbc.detection.domain;

import com.aliyun.openservices.aliyun.log.producer.Producer;
import com.aliyun.openservices.log.common.LogItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LogFraudNotifier {
    @Autowired
    Producer producer;
    @Value("${sls.projectName}")
    private String projectName;
    @Value("${sls.logStoreName}")
    private String logStoreName;

    public void notify(List<FraudWarning> fraudWarnings, FinishedCallBack finishedCallBack) {
        List<LogItem> logGroup = new ArrayList<LogItem>(fraudWarnings.size());
        fraudWarnings.forEach(
                warning -> {
                    LogItem logItem = new LogItem();
                    logItem.PushBack("transactionId", warning.getTransactionId());
                    logItem.PushBack("message", warning.getMessage());
                    logGroup.add(logItem);
                }
        );
        try {
            producer.send(projectName, logStoreName, logGroup,
                    result -> {
                        if (result.isSuccessful()) {
                            try {
                                finishedCallBack.call();
                                if(log.isDebugEnabled()) {
                                    log.debug(
                                            "Successfully sent log, project={}, logStore={}, logItem={}",
                                            projectName,
                                            logStoreName,
                                            logGroup.get(0).ToJsonString());
                                }
                            } catch (FinishedCallBack.FinishedCallBackException e) {
                                log.error("Error in finished callback", e);
                            }
                        } else {
                            log.error(
                                    "Failed to send log, project={}, logStore={}, logItem={}, result={}",
                                    projectName,
                                    logStoreName,
                                    logGroup.get(0).ToJsonString(),
                                    result);
                        }
            });
        } catch (Exception e) {
            log.error(
                    "Failed to send log, project={}, logStore={}, one of the logItems={}",
                    projectName,
                    logStoreName,
                    logGroup.get(0).ToJsonString(),
                    e);
        }
    }
}
