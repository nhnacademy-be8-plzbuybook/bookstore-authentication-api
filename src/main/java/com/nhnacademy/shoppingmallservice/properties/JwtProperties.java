
package com.nhnacademy.shoppingmallservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Setter
@Getter
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {
    private String secret;
    private Long accessExpirationTime;
    private Long refreshExpirationTime;
    private String tokenPrefix;
    private String headerString;
    private String loginUrl;
}

