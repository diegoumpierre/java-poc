package com.poc.notification.provider;

import com.poc.notification.domain.TenantConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class SmtpEmailProvider implements EmailProvider {

    @Override
    public void sendEmail(TenantConfig config, String to, String subject,
                          String textContent, String htmlContent,
                          String inReplyTo, String references) {
        Properties props = buildSmtpProperties(config);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getSmtpUsername(), config.getSmtpPassword());
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);

            String fromName = config.getFromName() != null ? config.getFromName() : config.getFromAddress();
            message.setFrom(new InternetAddress(config.getFromAddress(), fromName));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject != null ? subject : "");

            if (config.getReplyTo() != null) {
                message.setReplyTo(new InternetAddress[]{new InternetAddress(config.getReplyTo())});
            }

            if (inReplyTo != null) {
                message.setHeader("In-Reply-To", inReplyTo);
            }
            if (references != null) {
                message.setHeader("References", references);
            }

            if (htmlContent != null && !htmlContent.isBlank()) {
                message.setContent(htmlContent, "text/html; charset=UTF-8");
            } else {
                message.setText(textContent != null ? textContent : "", "UTF-8");
            }

            Transport.send(message);
            log.info("Email sent successfully to {} via SMTP", to);
        } catch (Exception e) {
            log.error("Failed to send email to {} via SMTP: {}", to, e.getMessage());
            throw new RuntimeException("SMTP send failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean testConnection(TenantConfig config) {
        Properties props = buildSmtpProperties(config);
        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getSmtpUsername(), config.getSmtpPassword());
                }
            });
            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.close();
            log.info("SMTP connection test successful for {}", config.getSmtpHost());
            return true;
        } catch (Exception e) {
            log.warn("SMTP connection test failed for {}: {}", config.getSmtpHost(), e.getMessage());
            return false;
        }
    }

    private Properties buildSmtpProperties(TenantConfig config) {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getSmtpHost());
        props.put("mail.smtp.port", String.valueOf(config.getSmtpPort()));
        props.put("mail.smtp.auth", "true");
        if (Boolean.TRUE.equals(config.getSmtpUseTls())) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        return props;
    }
}
