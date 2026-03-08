package com.poc.lar.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeignConfig {

    @Bean
    @Primary
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
}
