package com.nhnacademy.shoppingmallservice.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    @Test
    void testPasswordEncoderBean() {
        // Spring 컨텍스트 로딩
        ApplicationContext context = new AnnotationConfigApplicationContext(SecurityConfig.class);

        // PasswordEncoder 빈 가져오기
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);

        // BCryptPasswordEncoder 인스턴스인지 확인
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder,
                "PasswordEncoder는 BCryptPasswordEncoder 인스턴스여야 합니다.");
    }
}