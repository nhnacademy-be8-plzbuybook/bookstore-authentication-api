package com.nhnacademy.shoppingmallservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {
    private String secret;
    private Integer expirationTime;
    private Integer refreshExpirationTime;
    private String tokenPrefix;
    private String headerString;
    private String loginUrl;

    public String getSecret() {
        return secret;
    }

    public Integer getExpirationTime() {
        return expirationTime;
    }

    public Integer getRefreshExpirationTime() {
        return refreshExpirationTime;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public String getHeaderString() {
        return headerString;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setExpirationTime(Integer expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setRefreshExpirationTime(Integer refreshExpirationTime) {
        this.refreshExpirationTime = refreshExpirationTime;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public void setHeaderString(String headerString) {
        this.headerString = headerString;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
}
