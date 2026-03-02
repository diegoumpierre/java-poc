package com.poc.chat.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "whatsapp-service", url = "${app.feign.whatsapp-service-url}")
public interface WhatsAppServiceClient {

    @PostMapping("/api/whatsapp/messages/send-text")
    Map<String, Object> sendText(@RequestHeader("X-Tenant-Id") String tenantId,
                                  @RequestHeader("X-User-Id") String userId,
                                  @RequestBody Map<String, String> request);
}
