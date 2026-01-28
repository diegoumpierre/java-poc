package com.poc.auth.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic test to verify test infrastructure is working
 */
@DisplayName("Basic Test Infrastructure")
class BasicServiceTest {

    @Test
    @DisplayName("Should verify JUnit is working")
    void shouldVerifyJUnitIsWorking() {
        // Given
        String expected = "Test";

        // When
        String actual = "Test";

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should verify assertions are working")
    void shouldVerifyAssertionsAreWorking() {
        // Given
        int value = 42;

        // Then
        assertThat(value).isGreaterThan(0);
        assertThat(value).isEqualTo(42);
        assertThat(value).isNotNull();
    }
}
