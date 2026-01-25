package com.poc.auth.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * AccessContextResponse - Tudo que o frontend precisa para controle de acesso
 *
 * Retornado pelo endpoint GET /api/auth/context
 *
 * Frontend usa isso para:
 * - Saber quais botões/ações habilitar (via 'permissions')
 * - Saber quais features estão disponíveis (via 'entitlements')
 * - Verificar roles do usuário (via 'roles')
 *
 * Note: Menu deve ser buscado separadamente via organization-service /api/organizations/menus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessContextResponse {

    // =========================================================================
    // USER INFO
    // =========================================================================

    /** User ID */
    private UUID userId;

    /** User email */
    private String email;

    /** User full name */
    private String name;

    /** User nickname */
    private String nickname;

    /** Avatar URL (presigned) */
    private String avatar;

    // =========================================================================
    // TENANT INFO
    // =========================================================================

    /** Se o usuário pertence a pelo menos um tenant */
    private Boolean hasTenant;

    /** Tenant atual do usuário (pode ter múltiplos, mas 1 ativo por vez) */
    private TenantInfo currentTenant;

    /** Lista de tenants que o usuário pertence (para switch de tenant) */
    private List<TenantInfo> availableTenants;

    // =========================================================================
    // ACCESS CONTROL
    // =========================================================================

    /**
     * Roles do usuário NO TENANT ATUAL
     * Ex: ["ADMIN", "MANAGER"]
     */
    private Set<String> roles;

    /**
     * Permissions diretas do usuário (independente de tenant)
     * Ex: ["PLATFORM_ADMIN", "RESELLER_MANAGE"]
     */
    private Set<String> permissions;

    /**
     * Entitlements/Features do TENANT ATUAL (módulos contratados)
     * Ex: ["FINANCE_MODULE", "HELPDESK_MODULE", "BPF_MODULE"]
     *
     * Frontend usa para mostrar/ocultar menus FEATURE_GATED
     *
     * User-service returns entitlements as objects (EntitlementInfo),
     * this deserializer extracts featureCode strings for the frontend.
     */
    @JsonDeserialize(using = EntitlementSetDeserializer.class)
    private Set<String> entitlements;

    // =========================================================================
    // COMPUTED FLAGS (facilitam o frontend)
    // =========================================================================

    /** User é admin do tenant atual? (tem role ADMIN ou MANAGER) */
    private Boolean isAdmin;

    /** User é super admin da plataforma? (tem PLATFORM_ADMIN permission) */
    private Boolean isSuperAdmin;

    /** User é reseller? (pode gerenciar sub-tenants) */
    private Boolean isReseller;

    /** User é platform admin? */
    private Boolean isPlatformAdmin;

    /** User é customer no tenant atual? (tem role APENAS CUSTOMER) */
    private Boolean isCustomer;

    // =========================================================================
    // METADATA
    // =========================================================================

    /** Timestamp da geração do contexto (para cache no frontend) */
    private Instant generatedAt;

    /** TTL sugerido em segundos (frontend pode cachear por esse tempo) */
    private Integer cacheTtlSeconds;

    // =========================================================================
    // INNER CLASSES
    // =========================================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantInfo {
        private UUID id;
        private String name;
        private String slug;
        private String type;        // PLATFORM, RESELLER, CLIENT
        private String status;      // ACTIVE, SUSPENDED, TRIAL
        private UUID parentId;      // Para hierarquia
        private List<String> roles; // Roles do usuario neste tenant
    }
}
