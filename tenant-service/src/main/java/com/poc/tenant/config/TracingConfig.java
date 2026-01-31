package com.poc.tenant.config;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean
    @ConditionalOnMissingBean
    public OpenTelemetry openTelemetry() {
        return OpenTelemetrySdk.builder()
                .setTracerProvider(SdkTracerProvider.builder().build())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public Tracer micrometerTracer(OpenTelemetry openTelemetry) {
        return new OtelTracer(
                openTelemetry.getTracer("tenant-service"),
                new OtelCurrentTraceContext(),
                null
        );
    }
}
