package com.poc.chat.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "email-service", url = "${app.feign.email-service-url}")
public interface EmailServiceClient {

    @PostMapping("/api/email/messages/send")
    Map<String, Object> send(@RequestHeader("X-Tenant-Id") String tenantId,
                              @RequestHeader("X-User-Id") String userId,
                              @RequestBody Map<String, String> request);
}
