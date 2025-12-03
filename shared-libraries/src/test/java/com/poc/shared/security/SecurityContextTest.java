package com.poc.shared.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SecurityContextTest {

    @AfterEach
    void tearDown() {
        SecurityContext.clear();
    }

    @Test
    void getPermissions_whenNotSet_returnsEmptySet() {
        assertTrue(SecurityContext.getPermissions().isEmpty());
    }

    @Test
    void setPermissions_andGet_returnsCorrectSet() {
        SecurityContext.setPermissions(Set.of("KANBAN_MANAGE", "KANBAN_VIEW"));

        assertEquals(2, SecurityContext.getPermissions().size());
        assertTrue(SecurityContext.getPermissions().contains("KANBAN_MANAGE"));
        assertTrue(SecurityContext.getPermissions().contains("KANBAN_VIEW"));
    }

    @Test
    void clear_removesPermissions() {
        SecurityContext.setPermissions(Set.of("KANBAN_MANAGE"));
        SecurityContext.clear();

        assertTrue(SecurityContext.getPermissions().isEmpty());
    }

    @Test
    void hasPermission_whenPresent_returnsTrue() {
        SecurityContext.setPermissions(Set.of("KANBAN_MANAGE", "FINANCE_VIEW"));

        assertTrue(SecurityContext.hasPermission("KANBAN_MANAGE"));
        assertTrue(SecurityContext.hasPermission("FINANCE_VIEW"));
    }

    @Test
    void hasPermission_whenAbsent_returnsFalse() {
        SecurityContext.setPermissions(Set.of("KANBAN_MANAGE"));

        assertFalse(SecurityContext.hasPermission("FINANCE_MANAGE"));
    }

    @Test
    void hasPermission_whenEmpty_returnsFalse() {
        assertFalse(SecurityContext.hasPermission("KANBAN_MANAGE"));
    }

    @Test
    void hasAnyPermission_whenOneMatches_returnsTrue() {
        SecurityContext.setPermissions(Set.of("KANBAN_MANAGE"));

        assertTrue(SecurityContext.hasAnyPermission("KANBAN_MANAGE", "FINANCE_MANAGE"));
    }

    @Test
    void hasAnyPermission_whenNoneMatch_returnsFalse() {
        SecurityContext.setPermissions(Set.of("KANBAN_VIEW"));

        assertFalse(SecurityContext.hasAnyPermission("KANBAN_MANAGE", "FINANCE_MANAGE"));
    }

    @Test
    void hasAllPermissions_whenAllPresent_returnsTrue() {
        SecurityContext.setPermissions(Set.of("KANBAN_MANAGE", "FINANCE_MANAGE", "KANBAN_VIEW"));

        assertTrue(SecurityContext.hasAllPermissions("KANBAN_MANAGE", "FINANCE_MANAGE"));
    }

    @Test
    void hasAllPermissions_whenOneMissing_returnsFalse() {
        SecurityContext.setPermissions(Set.of("KANBAN_MANAGE"));

        assertFalse(SecurityContext.hasAllPermissions("KANBAN_MANAGE", "FINANCE_MANAGE"));
    }

    @Test
    void hasContext_whenPermissionsSet_returnsTrue() {
        SecurityContext.setPermissions(Set.of("KANBAN_MANAGE"));

        assertTrue(SecurityContext.hasContext());
    }

    @Test
    void hasContext_whenEmpty_returnsFalse() {
        SecurityContext.setPermissions(Set.of());

        assertFalse(SecurityContext.hasContext());
    }

    @Test
    void hasContext_whenNotSet_returnsFalse() {
        assertFalse(SecurityContext.hasContext());
    }
}
