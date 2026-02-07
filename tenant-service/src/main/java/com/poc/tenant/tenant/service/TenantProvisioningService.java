package com.poc.tenant.tenant.service;

import com.poc.tenant.event.TenantProvisioningEventPublisher;
import com.poc.tenant.membership.model.MembershipRequest;
import com.poc.tenant.membership.service.MembershipService;
import com.poc.tenant.model.request.CreateClientRequest;
import com.poc.tenant.model.request.CreatePartnerRequest;
import com.poc.tenant.model.response.TenantProvisioningResult;
import com.poc.tenant.model.response.TenantProvisioningResult.ProvisioningStep;
import com.poc.tenant.model.response.TenantResponse;
import com.poc.tenant.tenant.domain.Tenant;
import com.poc.tenant.tenant.repository.TenantRepository;
import com.poc.tenant.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles tenant provisioning with async user creation via Kafka events.
 *
 * Flow (Path B - admin-initiated provisioning):
 *   1. Create tenant (synchronous, local)
 *   2. Publish PROVISION_USER_REQUESTED event to Kafka (async)
 *      - user-service consumes this, creates user, publishes USER_CREATED_FOR_TENANT
 *   3. Publish TENANT_PROVISIONED event to Kafka (async)
 *      - billing-service consumes this, creates subscription if planId is present
 *   4. UserEventConsumer receives USER_CREATED_FOR_TENANT -> creates membership -> status = COMPLETE
 *
 * The provisioning status starts as "IN_PROGRESS" and is updated to "COMPLETE" by UserEventConsumer
 * when the user is created and membership is established.
 *
 * NOTE: Subscription creation is now fully async via billing-events.
 * BillingClient was removed - no more synchronous Feign calls to billing-service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantProvisioningService {

    static final UUID ADMIN_ROLE_ID = UUID.fromString("22222222-2222-2222-2222-222222222223");

    private final TenantRepository tenantRepository;
    private final MembershipService membershipService;
    private final PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private TenantProvisioningEventPublisher provisioningEventPublisher;

    @Transactional
    public TenantProvisioningResult provisionPartner(CreatePartnerRequest request) {
        List<ProvisioningStep> steps = new ArrayList<>();

        // Step 1: Create tenant
        if (tenantRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Tenant with slug already exists: " + request.getSlug());
        }

        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .slug(request.getSlug())
                .parentTenantId(Tenant.PLATFORM_TENANT_ID)
                .tenantType(Tenant.TYPE_RESELLER)
                .status(Tenant.STATUS_ACTIVE)
                .subscriptionStatus("ACTIVE")
                .provisioningStatus("IN_PROGRESS")
                .billingModel(request.getBillingModel() != null ? request.getBillingModel() : Tenant.BILLING_DIRECT)
                .commissionRate(request.getCommissionRate())
                .wholesaleDiscount(request.getWholesaleDiscount())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        tenant = tenantRepository.save(tenant);
        steps.add(ProvisioningStep.success("CREATE_TENANT"));
        log.info("Provisioning partner: {} (id={})", tenant.getSlug(), tenant.getId());

        // Step 2: Publish provisioning event for async user creation via Kafka
        // Password is encoded BEFORE publishing to avoid sending plaintext through Kafka
        try {
            String encodedPassword = passwordEncoder.encode(request.getAdminPassword());

            if (provisioningEventPublisher != null) {
                provisioningEventPublisher.publishProvisionUserRequested(
                        tenant.getId(), tenant.getName(), tenant.getSlug(),
                        request.getAdminEmail(), encodedPassword, request.getAdminName(),
                        request.getPlanId());
                steps.add(ProvisioningStep.success("PUBLISH_USER_CREATION_EVENT"));
                log.info("Published PROVISION_USER_REQUESTED for tenant {} (admin: {})",
                        tenant.getId(), request.getAdminEmail());
            } else {
                log.warn("Kafka disabled - cannot publish provisioning event for tenant {}", tenant.getId());
                steps.add(ProvisioningStep.failed("PUBLISH_USER_CREATION_EVENT", "Kafka disabled"));
            }
        } catch (Exception e) {
            log.error("Failed to publish provisioning event for tenant {}: {}", tenant.getId(), e.getMessage());
            steps.add(ProvisioningStep.failed("PUBLISH_USER_CREATION_EVENT", e.getMessage()));
        }

        // Step 3: Membership will be created asynchronously by UserEventConsumer
        // when user-service publishes USER_CREATED_FOR_TENANT
        steps.add(ProvisioningStep.pending("CREATE_ADMIN_USER"));
        steps.add(ProvisioningStep.pending("CREATE_MEMBERSHIP"));

        // Step 4: Publish TENANT_PROVISIONED event for billing-service to create subscription
        if (request.getPlanId() != null) {
            try {
                if (provisioningEventPublisher != null) {
                    provisioningEventPublisher.publishTenantProvisioned(
                            tenant.getId(), tenant.getName(), tenant.getSlug(),
                            request.getPlanId(), null, request.getAdminEmail());
                    steps.add(ProvisioningStep.pending("CREATE_SUBSCRIPTION"));
                    log.info("Published TENANT_PROVISIONED for tenant {} (planId: {})",
                            tenant.getId(), request.getPlanId());
                } else {
                    log.warn("Kafka disabled - cannot publish TENANT_PROVISIONED for tenant {}", tenant.getId());
                    steps.add(ProvisioningStep.failed("CREATE_SUBSCRIPTION", "Kafka disabled"));
                }
            } catch (Exception e) {
                log.error("Failed to publish TENANT_PROVISIONED for tenant {}: {}", tenant.getId(), e.getMessage());
                steps.add(ProvisioningStep.failed("CREATE_SUBSCRIPTION", e.getMessage()));
            }
        }

        // Status stays IN_PROGRESS - will be updated to COMPLETE by UserEventConsumer
        tenant.setUpdatedAt(Instant.now());
        tenant.markNotNew();
        tenantRepository.save(tenant);

        return TenantProvisioningResult.builder()
                .tenant(TenantResponse.from(tenant))
                .provisioningStatus("IN_PROGRESS")
                .steps(steps)
                .build();
    }

    @Transactional
    public TenantProvisioningResult provisionClient(CreateClientRequest request, UUID parentId) {
        List<ProvisioningStep> steps = new ArrayList<>();

        // Step 1: Create tenant
        if (tenantRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Tenant with slug already exists: " + request.getSlug());
        }

        UUID effectiveParentId = parentId != null ? parentId : Tenant.PLATFORM_TENANT_ID;

        Tenant parent = tenantRepository.findById(effectiveParentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent tenant not found: " + effectiveParentId));

        if (Tenant.TYPE_CLIENT.equals(parent.getTenantType())) {
            throw new IllegalArgumentException("Cannot create client under another client");
        }

        Instant trialEndsAt = null;
        String subscriptionStatus = "ACTIVE";
        if (request.getTrialDays() != null && request.getTrialDays() > 0) {
            trialEndsAt = Instant.now().plus(request.getTrialDays(), ChronoUnit.DAYS);
            subscriptionStatus = "TRIAL";
        }

        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .slug(request.getSlug())
                .parentTenantId(effectiveParentId)
                .tenantType(Tenant.TYPE_CLIENT)
                .status(Tenant.STATUS_ACTIVE)
                .subscriptionStatus(subscriptionStatus)
                .trialEndsAt(trialEndsAt)
                .provisioningStatus("IN_PROGRESS")
                .billingModel(request.getBillingModel() != null ? request.getBillingModel() : Tenant.BILLING_DIRECT)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        tenant = tenantRepository.save(tenant);
        steps.add(ProvisioningStep.success("CREATE_TENANT"));
        log.info("Provisioning client: {} (id={}, parent={})", tenant.getSlug(), tenant.getId(), effectiveParentId);

        // Step 2: Publish provisioning event for async user creation via Kafka
        try {
            String encodedPassword = passwordEncoder.encode(request.getAdminPassword());

            if (provisioningEventPublisher != null) {
                provisioningEventPublisher.publishProvisionUserRequested(
                        tenant.getId(), tenant.getName(), tenant.getSlug(),
                        request.getAdminEmail(), encodedPassword, request.getAdminName(),
                        request.getPlanId());
                steps.add(ProvisioningStep.success("PUBLISH_USER_CREATION_EVENT"));
                log.info("Published PROVISION_USER_REQUESTED for tenant {} (admin: {})",
                        tenant.getId(), request.getAdminEmail());
            } else {
                log.warn("Kafka disabled - cannot publish provisioning event for tenant {}", tenant.getId());
                steps.add(ProvisioningStep.failed("PUBLISH_USER_CREATION_EVENT", "Kafka disabled"));
            }
        } catch (Exception e) {
            log.error("Failed to publish provisioning event for tenant {}: {}", tenant.getId(), e.getMessage());
            steps.add(ProvisioningStep.failed("PUBLISH_USER_CREATION_EVENT", e.getMessage()));
        }

        // Step 3: Membership will be created asynchronously by UserEventConsumer
        steps.add(ProvisioningStep.pending("CREATE_ADMIN_USER"));
        steps.add(ProvisioningStep.pending("CREATE_MEMBERSHIP"));

        // Step 4: Publish TENANT_PROVISIONED event for billing-service to create subscription
        if (request.getPlanId() != null) {
            try {
                if (provisioningEventPublisher != null) {
                    provisioningEventPublisher.publishTenantProvisioned(
                            tenant.getId(), tenant.getName(), tenant.getSlug(),
                            request.getPlanId(), null, request.getAdminEmail());
                    steps.add(ProvisioningStep.pending("CREATE_SUBSCRIPTION"));
                    log.info("Published TENANT_PROVISIONED for tenant {} (planId: {})",
                            tenant.getId(), request.getPlanId());
                } else {
                    log.warn("Kafka disabled - cannot publish TENANT_PROVISIONED for tenant {}", tenant.getId());
                    steps.add(ProvisioningStep.failed("CREATE_SUBSCRIPTION", "Kafka disabled"));
                }
            } catch (Exception e) {
                log.error("Failed to publish TENANT_PROVISIONED for tenant {}: {}", tenant.getId(), e.getMessage());
                steps.add(ProvisioningStep.failed("CREATE_SUBSCRIPTION", e.getMessage()));
            }
        }

        // Status stays IN_PROGRESS - will be updated to COMPLETE by UserEventConsumer
        tenant.setUpdatedAt(Instant.now());
        tenant.markNotNew();
        tenantRepository.save(tenant);

        return TenantProvisioningResult.builder()
                .tenant(TenantResponse.from(tenant))
                .provisioningStatus("IN_PROGRESS")
                .steps(steps)
                .build();
    }

    /**
     * Complete provisioning after receiving USER_CREATED_FOR_TENANT event.
     * Called by UserEventConsumer.
     */
    @Transactional
    public void completeProvisioningWithUser(UUID tenantId, UUID userId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantId));

        if (!"IN_PROGRESS".equals(tenant.getProvisioningStatus())) {
            log.warn("Tenant {} provisioning is not IN_PROGRESS (status={}), skipping membership creation",
                    tenantId, tenant.getProvisioningStatus());
            return;
        }

        // Create membership locally
        try {
            var membershipResponse = membershipService.addMember(tenantId,
                    new MembershipRequest(userId, List.of(ADMIN_ROLE_ID)));
            log.info("Created membership {} for user {} in tenant {} (async provisioning)",
                    membershipResponse.getId(), userId, tenantId);
        } catch (Exception e) {
            log.error("Failed to create membership for user {} in tenant {} during async provisioning: {}",
                    userId, tenantId, e.getMessage());
            // Set status to PARTIAL so it can be retried/investigated
            tenant.markNotNew();
            tenant.setProvisioningStatus("PARTIAL");
            tenant.setUpdatedAt(Instant.now());
            tenantRepository.save(tenant);
            return;
        }

        // Update provisioning status to COMPLETE
        tenant.markNotNew();
        tenant.setProvisioningStatus("COMPLETE");
        tenant.setUpdatedAt(Instant.now());
        tenantRepository.save(tenant);
        log.info("Tenant {} provisioning COMPLETE (async)", tenantId);
    }
}
