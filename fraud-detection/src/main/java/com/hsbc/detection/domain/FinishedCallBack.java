package com.hsbc.detection.domain;

public interface FinishedCallBack {
    void call() throws FinishedCallBackException;

    class FinishedCallBackException extends Exception {
        public FinishedCallBackException(String message) {
            super(message);
        }

        public FinishedCallBackException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
