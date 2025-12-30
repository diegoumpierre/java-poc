package com.poc.gateway.controller;

import com.poc.gateway.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProxyController {

    private final ProxyService proxyService;

    @RequestMapping("/api/**")
    public Mono<ResponseEntity<byte[]>> proxy(ServerHttpRequest request) {
        return proxyService.forward(request);
    }
}
