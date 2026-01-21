package com.poc.auth.config;

import com.poc.auth.domain.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.mapping.event.AfterConvertCallback;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.UUID;

@Configuration
public class JdbcConfig {

    @Bean
    public NamingStrategy namingStrategy() {
        return new NamingStrategy() {
            @Override
            public String getTableName(Class<?> type) {
                // Use @Table annotation value if present, otherwise convert to UPPER_SNAKE_CASE
                org.springframework.data.relational.core.mapping.Table tableAnnotation =
                    type.getAnnotation(org.springframework.data.relational.core.mapping.Table.class);
                if (tableAnnotation != null && !tableAnnotation.value().isEmpty()) {
                    return tableAnnotation.value();
                }
                return toUpperSnakeCase(type.getSimpleName());
            }

            @Override
            public String getColumnName(RelationalPersistentProperty property) {
                // Use @Column annotation value if present, otherwise convert to UPPER_SNAKE_CASE
                org.springframework.data.relational.core.mapping.Column columnAnnotation =
                    property.findAnnotation(org.springframework.data.relational.core.mapping.Column.class);
                if (columnAnnotation != null && !columnAnnotation.value().isEmpty()) {
                    return columnAnnotation.value();
                }
                return toUpperSnakeCase(property.getName());
            }

            private String toUpperSnakeCase(String name) {
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < name.length(); i++) {
                    char c = name.charAt(i);
                    if (Character.isUpperCase(c) && i > 0) {
                        result.append('_');
                    }
                    result.append(Character.toUpperCase(c));
                }
                return result.toString();
            }
        };
    }

    @Bean
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(
            new UuidToStringConverter(),
            new StringToUuidConverter(),
            new IntegerToBooleanConverter(),
            new BooleanToIntegerConverter()
        ));
    }

    @Bean
    AfterConvertCallback<VerificationCode> afterVerificationCodeConvertCallback() {
        return verificationCode -> {
            verificationCode.markNotNew();
            return verificationCode;
        };
    }

    @Bean
    AfterConvertCallback<RefreshToken> afterRefreshTokenConvertCallback() {
        return refreshToken -> {
            refreshToken.markNotNew();
            return refreshToken;
        };
    }

    @Bean
    AfterConvertCallback<AuditLog> afterAuditLogConvertCallback() {
        return auditLog -> {
            auditLog.markNotNew();
            return auditLog;
        };
    }

    @Bean
    AfterConvertCallback<TwoFactorCode> afterTwoFactorCodeConvertCallback() {
        return twoFactorCode -> {
            twoFactorCode.markNotNew();
            return twoFactorCode;
        };
    }

    @Bean
    AfterConvertCallback<UserSession> afterUserSessionConvertCallback() {
        return userSession -> {
            userSession.markNotNew();
            return userSession;
        };
    }

    @WritingConverter
    static class UuidToStringConverter implements Converter<UUID, String> {
        @Override
        public String convert(@NonNull UUID source) {
            return source.toString();
        }
    }

    @ReadingConverter
    static class StringToUuidConverter implements Converter<String, UUID> {
        @Override
        public UUID convert(@NonNull String source) {
            return UUID.fromString(source);
        }
    }

    @ReadingConverter
    static class IntegerToBooleanConverter implements Converter<Integer, Boolean> {
        @Override
        public Boolean convert(@NonNull Integer source) {
            return source != 0;
        }
    }

    @WritingConverter
    static class BooleanToIntegerConverter implements Converter<Boolean, Integer> {
        @Override
        public Integer convert(@NonNull Boolean source) {
            return source ? 1 : 0;
        }
    }
}
