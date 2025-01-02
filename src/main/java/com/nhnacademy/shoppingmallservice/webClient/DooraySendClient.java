package com.nhnacademy.shoppingmallservice.webClient;

import com.nhnacademy.shoppingmallservice.dto.MessagePayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "DooraySendClient", url = "https://hook.dooray.com/services")
public interface DooraySendClient {

    @PostMapping("/{serviceId}/{botId}/{botToken}")
    void sendMessage(@RequestBody MessagePayload messagePayload,
                     @PathVariable Long serviceId,
                     @PathVariable Long botId,
                     @PathVariable String botToken);
}
