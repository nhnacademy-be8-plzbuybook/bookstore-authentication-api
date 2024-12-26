package com.nhnacademy.shoppingmallservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//테스트 시 프로퍼티 값 읽도록 설정
@SpringBootTest(properties =
        "spring.config.location = " +
                "file:/app/application-secret.yml")
class ShoppingmallServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
