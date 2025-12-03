package com.poc.shared.security;

import com.poc.shared.exception.PermissionDeniedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityAspectTest {

    private SecurityAspect securityAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    @BeforeEach
    void setUp() {
        securityAspect = new SecurityAspect();
    }

    private void setupMocks(String methodName) throws Exception {
        Method method = TestController.class.getMethod(methodName);
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        lenient().when(signature.getMethod()).thenReturn(method);
        lenient().when(joinPoint.getTarget()).thenReturn(new TestController());
    }

    @AfterEach
    void tearDown() {
        SecurityContext.clear();
    }

    @Test
    void validatePermission_withMatchingPermission_proceeds() throws Throwable {
        SecurityContext.setPermissions(Set.of("KANBAN_MANAGE"));
        setupMocks("manageBoardSingle");
        when(joinPoint.proceed()).thenReturn("success");

        Object result = securityAspect.validatePermission(joinPoint);

        assertEquals("success", result);
        verify(joinPoint).proceed();
    }

    @Test
    void validatePermission_withoutPermission_throwsPermissionDenied() throws Throwable {
        SecurityContext.setPermissions(Set.of("FINANCE_VIEW"));
        setupMocks("manageBoardSingle");

        PermissionDeniedException ex = assertThrows(
                PermissionDeniedException.class,
                () -> securityAspect.validatePermission(joinPoint)
        );

        assertTrue(ex.getRequiredPermission().contains("KANBAN_MANAGE"));
        verify(joinPoint, never()).proceed();
    }

    @Test
    void validatePermission_orMode_anyPermissionSuffices() throws Throwable {
        SecurityContext.setPermissions(Set.of("FINANCE_APPROVE"));
        setupMocks("manageFinanceOr");
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = securityAspect.validatePermission(joinPoint);

        assertEquals("ok", result);
    }

    @Test
    void validatePermission_andMode_requiresAll() throws Throwable {
        SecurityContext.setPermissions(Set.of("FINANCE_MANAGE"));
        setupMocks("manageFinanceAnd");

        assertThrows(PermissionDeniedException.class,
                () -> securityAspect.validatePermission(joinPoint));
    }

    @Test
    void validatePermission_andMode_withAllPermissions_proceeds() throws Throwable {
        SecurityContext.setPermissions(Set.of("FINANCE_MANAGE", "FINANCE_APPROVE"));
        setupMocks("manageFinanceAnd");
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = securityAspect.validatePermission(joinPoint);

        assertEquals("ok", result);
    }

    @Test
    void validatePermission_emptyPermissions_throwsPermissionDenied() throws Throwable {
        SecurityContext.setPermissions(Set.of());
        setupMocks("manageBoardSingle");

        assertThrows(PermissionDeniedException.class,
                () -> securityAspect.validatePermission(joinPoint));
    }

    @Test
    void validatePermission_noAnnotation_proceeds() throws Throwable {
        setupMocks("noAnnotation");
        when(joinPoint.proceed()).thenReturn("no-check");

        Object result = securityAspect.validatePermission(joinPoint);

        assertEquals("no-check", result);
    }

    // Test controller with annotated methods for testing
    static class TestController {

        @RequiresPermission("KANBAN_MANAGE")
        public String manageBoardSingle() {
            return "ok";
        }

        @RequiresPermission({"FINANCE_MANAGE", "FINANCE_APPROVE"})
        public String manageFinanceOr() {
            return "ok";
        }

        @RequiresPermission(value = {"FINANCE_MANAGE", "FINANCE_APPROVE"}, allRequired = true)
        public String manageFinanceAnd() {
            return "ok";
        }

        public String noAnnotation() {
            return "ok";
        }
    }
}
