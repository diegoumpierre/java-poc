package com.poc.shared.jdbc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * JDBC converters for Boolean <-> Integer conversion.
 * Required because MySQL stores BOOLEAN as TINYINT(1).
 *
 * <p>IMPORTANT: Use Boolean (wrapper) not boolean (primitive) in entities.</p>
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
public final class BooleanConverters {

    private BooleanConverters() {
        // Utility class
    }

    public static Converter<Integer, Boolean> reading() {
        return new IntegerToBooleanConverter();
    }

    public static Converter<Boolean, Integer> writing() {
        return new BooleanToIntegerConverter();
    }

    @ReadingConverter
    public static class IntegerToBooleanConverter implements Converter<Integer, Boolean> {
        @Override
        public Boolean convert(@NonNull Integer source) {
            return source != 0;
        }
    }

    @WritingConverter
    public static class BooleanToIntegerConverter implements Converter<Boolean, Integer> {
        @Override
        public Integer convert(@NonNull Boolean source) {
            return source ? 1 : 0;
        }
    }
}
