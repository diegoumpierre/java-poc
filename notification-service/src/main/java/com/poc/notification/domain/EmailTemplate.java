package com.poc.notification.domain;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    PASSWORD_RESET("password-reset", "Password Reset Request"),
    WELCOME("welcome", "Welcome to Softwares101!"),
    VERIFICATION("verification", "Verify Your Email Address"),
    SIMPLE("simple", "Notification"),

    // Onboarding templates
    INVITE("invite", "You're Invited to Join"),
    ACCESS_REQUEST("access-request", "New Access Request"),
    ACCESS_REQUEST_APPROVED("access-request-approved", "Your Access Request was Approved"),
    ACCESS_REQUEST_REJECTED("access-request-rejected", "Your Access Request Status"),

    // Billing templates
    INVOICE_CREATED("invoice-created", "Nova Fatura Disponivel"),
    INVOICE_REMINDER("invoice-reminder", "Lembrete de Fatura"),
    INVOICE_OVERDUE("invoice-overdue", "Fatura Vencida"),
    PAYMENT_CONFIRMED("payment-confirmed", "Pagamento Confirmado"),
    COMMISSION_READY("commission-ready", "Comissao Calculada"),
    COMMISSION_PAID("commission-paid", "Comissao Paga"),
    SUBSCRIPTION_SUSPENDED("subscription-suspended", "Conta Suspensa"),

    // Signature templates
    SIGNATURE_INVITE("signature-invite", "Documento para Assinatura"),
    SIGNATURE_REMINDER("signature-reminder", "Lembrete: Documento Aguardando Assinatura"),
    SIGNATURE_COMPLETED("signature-completed", "Documento Assinado com Sucesso"),
    SIGNATURE_REFUSED("signature-refused", "Assinatura Recusada"),
    SIGNATURE_CANCELLED("signature-cancelled", "Assinatura Cancelada"),
    SIGNATURE_ALL_SIGNED("signature-all-signed", "Todas as Assinaturas Concluidas");

    private final String templateName;
    private final String defaultSubject;

    EmailTemplate(String templateName, String defaultSubject) {
        this.templateName = templateName;
        this.defaultSubject = defaultSubject;
    }
}
