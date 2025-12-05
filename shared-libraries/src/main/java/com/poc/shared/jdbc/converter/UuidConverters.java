package com.poc.shared.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import java.util.UUID;

/**
 * JDBC converters for UUID <-> String conversion.
 * Required because MySQL stores UUIDs as CHAR(36).
 *
 * <p>Usage in JdbcConfig:</p>
 * <pre>
 * &#64;Bean
 * public JdbcCustomConversions jdbcCustomConversions() {
 *     return new JdbcCustomConversions(Arrays.asList(
 *         UuidConverters.writing(),
 *         UuidConverters.reading(),
 *         BooleanConverters.reading(),
 *         BooleanConverters.writing()
 *     ));
 * }
 * </pre>
 */
public final class UuidConverters {

    private UuidConverters() {
        // Utility class
    }

    public static Converter<UUID, String> writing() {
        return new UuidToStringConverter();
    }

    public static Converter<String, UUID> reading() {
        return new StringToUuidConverter();
    }

    @WritingConverter
    public static class UuidToStringConverter implements Converter<UUID, String> {
        @Override
        public String convert(@NonNull UUID source) {
            return source.toString();
        }
    }

    @ReadingConverter
    public static class StringToUuidConverter implements Converter<String, UUID> {
        @Override
        public UUID convert(@NonNull String source) {
            return UUID.fromString(source);
        }
    }
}
