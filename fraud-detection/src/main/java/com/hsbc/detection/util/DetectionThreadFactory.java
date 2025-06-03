package com.hsbc.detection.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

@Slf4j
public class DetectionThreadFactory implements ThreadFactory {
    private int threadCount = 0;
    private final String threadNamePrefix;

    public DetectionThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(new RunnableWithExceptionProtection(r, t -> {
            log.error(t.getMessage(), t);
        }));
        thread.setName(threadNamePrefix + (++threadCount));
        thread.setDaemon(false);
        return thread;
    }
}
