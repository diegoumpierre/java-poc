package com.poc.shared.jdbc;

import com.poc.shared.jdbc.converter.BooleanConverters;
import com.poc.shared.jdbc.converter.UuidConverters;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.List;

/**
 * Provides standard JDBC converters for all services.
 *
 * <p>Usage in service's JdbcConfig:</p>
 * <pre>
 * &#64;Configuration
 * public class JdbcConfig {
 *     &#64;Bean
 *     public JdbcCustomConversions jdbcCustomConversions() {
 *         return new JdbcCustomConversions(JdbcConvertersConfig.getStandardConverters());
 *     }
 * }
 * </pre>
 */
public final class JdbcConvertersConfig {

    private JdbcConvertersConfig() {
        // Utility class
    }

    /**
     * Returns the standard list of converters for MySQL compatibility.
     * Includes UUID and Boolean converters.
     */
    public static List<Converter<?, ?>> getStandardConverters() {
        return Arrays.asList(
            UuidConverters.writing(),
            UuidConverters.reading(),
            BooleanConverters.reading(),
            BooleanConverters.writing()
        );
    }
}
