package com.nhnacademy.shoppingmallservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ConfigurationProperties(prefix = "oauth.payco")
@Component
public class PaycoOauthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String responseType;
    private String codeUrl;
    private String tokenUrl;
    private String userInfoUrl;
    private String grantType;
}
