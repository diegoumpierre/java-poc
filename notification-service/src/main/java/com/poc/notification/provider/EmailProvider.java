package com.poc.notification.provider;

import com.poc.notification.domain.TenantConfig;

public interface EmailProvider {

    void sendEmail(TenantConfig config, String to, String subject,
                   String textContent, String htmlContent,
                   String inReplyTo, String references);

    boolean testConnection(TenantConfig config);
}
