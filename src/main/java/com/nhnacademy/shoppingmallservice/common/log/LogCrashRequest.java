package com.nhnacademy.shoppingmallservice.common.log;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogCrashRequest {
    private String projectName;
    private String projectVersion;
    private String logVersion;
    private String body;
    private String sendTime;
    private String logSource;
    private String logType;
    private String category;
    private String errorCodeType;
    private String host;
    private String logLevel;

    private static final String DEFAULT_PROJECT_NAME = "nMWnKdBvAFvUW8XL";
    private static final String DEFAULT_PROJECT_VERSION = "1.0.0";
    private static final String DEFAULT_LOG_VERSION = "v2";
    private static final String DEFAULT_LOG_LEVEL= "WARN";
    private static final String DEFAULT_LOG_SOURCE = "plzbuybook-auth";
    private static final String DEFAULT_LOG_TYPE = "log";
    private static final String DEFAULT_HOST = "plzbuybook";

    public LogCrashRequest(String body) {
        this.body = body;
        this.projectName = DEFAULT_PROJECT_NAME;
        this.projectVersion = DEFAULT_PROJECT_VERSION;
        this.logVersion = DEFAULT_LOG_VERSION;
        this.logSource = DEFAULT_LOG_SOURCE;
        this.logType = DEFAULT_LOG_TYPE;
        this.host = DEFAULT_HOST;
        this.logLevel = DEFAULT_LOG_LEVEL;
    }
}
