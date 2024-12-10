package com.nhnacademy.shoppingmallservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ConfigurationProperties(prefix = "jwt")
@Configuration
public class JwtProperties {
    private String secret;
    private Integer expirationTime;
    private String tokenPrefix;
    private String headerString;
    private String loginUrl;

}
