package com.poc.tenant.membership.service;

import com.poc.tenant.exception.BusinessException;
import com.poc.tenant.exception.ResourceNotFoundException;
import com.poc.tenant.membership.domain.Membership;
import com.poc.tenant.membership.domain.MembershipRole;
import com.poc.tenant.membership.event.MembershipEventPublisher;
import com.poc.tenant.membership.model.MembershipRequest;
import com.poc.tenant.membership.model.MembershipResponse;
import com.poc.tenant.membership.repository.MembershipRepository;
import com.poc.tenant.membership.repository.MembershipRoleRepository;
import com.poc.tenant.security.TenantContext;
import com.poc.tenant.tenant.domain.Tenant;
import com.poc.tenant.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipRoleRepository membershipRoleRepository;
    private final TenantRepository tenantRepository;

    @Autowired(required = false)
    private MembershipEventPublisher eventPublisher;

    public List<MembershipResponse> findAll() {
        List<Membership> memberships = StreamSupport.stream(membershipRepository.findAll().spliterator(), false)
                .filter(m -> !"DELETED".equals(m.getStatus()))
                .collect(Collectors.toList());

        return enrichWithTenantInfo(memberships);
    }

    public List<MembershipResponse> findByTenantId(UUID tenantId) {
        List<Membership> memberships = membershipRepository.findActiveByTenantId(tenantId);
        return enrichWithTenantInfo(memberships);
    }

    public List<MembershipResponse> findByUserId(UUID userId) {
        List<Membership> memberships = membershipRepository.findActiveByUserId(userId);
        return enrichWithTenantInfo(memberships);
    }

    private List<MembershipResponse> enrichWithTenantInfo(List<Membership> memberships) {
        List<UUID> tenantIds = memberships.stream()
                .map(Membership::getTenantId)
                .distinct()
                .collect(Collectors.toList());

        Map<UUID, Tenant> tenantMap = tenantRepository.findAllByIdIn(tenantIds).stream()
                .collect(Collectors.toMap(Tenant::getId, Function.identity()));

        return memberships.stream()
                .map(m -> {
                    MembershipResponse response = toResponse(m);
                    Tenant tenant = tenantMap.get(m.getTenantId());
                    if (tenant != null) {
                        response.setTenant(MembershipResponse.TenantInfo.builder()
                                .id(tenant.getId())
                                .name(tenant.getName())
                                .slug(tenant.getSlug())
                                .tenantType(tenant.getTenantType())
                                .status(tenant.getStatus())
                                .parentTenantId(tenant.getParentTenantId())
                                .build());
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    public MembershipResponse findById(UUID id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found: " + id));
        return toResponse(membership);
    }

    @Transactional
    public MembershipResponse addMember(UUID tenantId, MembershipRequest request) {
        if (membershipRepository.existsByUserIdAndTenantId(request.getUserId(), tenantId)) {
            throw new BusinessException("User is already a member of this tenant");
        }

        Membership membership = Membership.builder()
                .id(UUID.randomUUID())
                .userId(request.getUserId())
                .tenantId(tenantId)
                .status("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        membership = membershipRepository.save(membership);

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            for (UUID roleId : request.getRoleIds()) {
                membershipRoleRepository.insertMembershipRole(membership.getId(), roleId);
            }
        }

        log.info("Added member {} to tenant {}", request.getUserId(), tenantId);

        if (eventPublisher != null) {
            eventPublisher.publishMembershipCreated(membership.getId(), request.getUserId(), tenantId);
        }

        return toResponse(membership);
    }

    @Transactional
    public void removeMember(UUID tenantId, UUID userId) {
        Membership membership = membershipRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        UUID currentUser = TenantContext.getUserId();
        membership.softDelete(currentUser);
        membership.markNotNew();
        membershipRepository.save(membership);

        log.info("Removed member {} from tenant {}", userId, tenantId);

        if (eventPublisher != null) {
            eventPublisher.publishMembershipDeleted(membership.getId(), userId, tenantId);
        }
    }

    @Transactional
    public MembershipResponse updateRoles(UUID membershipId, List<UUID> roleIds) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found: " + membershipId));

        membershipRoleRepository.deleteByMembershipId(membershipId);

        if (roleIds != null) {
            for (UUID roleId : roleIds) {
                membershipRoleRepository.insertMembershipRole(membershipId, roleId);
            }
        }

        membership.setUpdatedAt(Instant.now());
        membership.markNotNew();
        membershipRepository.save(membership);

        log.info("Updated roles for membership {}", membershipId);

        if (eventPublisher != null) {
            eventPublisher.publishMembershipRolesUpdated(membershipId, membership.getUserId(), membership.getTenantId());
        }

        return toResponse(membership);
    }

    private MembershipResponse toResponse(Membership membership) {
        List<UUID> roleIds = membershipRoleRepository.findByMembershipId(membership.getId())
                .stream()
                .map(MembershipRole::getRoleId)
                .collect(Collectors.toList());

        return MembershipResponse.builder()
                .id(membership.getId())
                .userId(membership.getUserId())
                .tenantId(membership.getTenantId())
                .status(membership.getStatus())
                .isOwner(membership.getIsOwner())
                .roleIds(roleIds)
                .createdAt(membership.getCreatedAt())
                .build();
    }
}
