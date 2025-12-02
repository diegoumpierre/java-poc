package com.poc.shared.feign;

import com.poc.shared.security.SecurityContext;
import com.poc.shared.tenant.TenantContext;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class SharedFeignConfig {

    @Bean
    public RequestInterceptor tenantHeaderInterceptor() {
        return requestTemplate -> {
            if (TenantContext.getCurrentTenant() != null) {
                requestTemplate.header("X-Tenant-Id", TenantContext.getCurrentTenant().toString());
            }
            if (TenantContext.getCurrentUser() != null) {
                requestTemplate.header("X-User-Id", TenantContext.getCurrentUser().toString());
            }
            if (TenantContext.getCurrentMembership() != null) {
                requestTemplate.header("X-Membership-Id", TenantContext.getCurrentMembership().toString());
            }
            Set<String> permissions = SecurityContext.getPermissions();
            if (!permissions.isEmpty()) {
                requestTemplate.header("X-User-Permissions", String.join(",", permissions));
            }
        };
    }
}
