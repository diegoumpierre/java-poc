package com.poc.tenant.membership.service;

import com.poc.tenant.BaseUnitTest;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("MembershipService Unit Tests")
class MembershipServiceUnitTest extends BaseUnitTest {

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MembershipRoleRepository membershipRoleRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private MembershipEventPublisher eventPublisher;

    @InjectMocks
    private MembershipService service;

    @BeforeEach
    void setUpLocalTenantContext() {
        // MembershipService uses com.poc.tenant.security.TenantContext (local),
        // not the shared one from BaseUnitTest
        TenantContext.setTenantId(TEST_TENANT_ID);
        TenantContext.setUserId(TEST_USER_ID);

        // eventPublisher uses @Autowired(required=false), not constructor injection,
        // so @InjectMocks doesn't inject it. Set it manually.
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);
    }

    @AfterEach
    void clearLocalTenantContext() {
        TenantContext.clear();
    }

    private static final UUID ROLE_ADMIN_ID = UUID.fromString("22222222-2222-2222-2222-222222222223");
    private static final UUID ROLE_USER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private Membership buildMembership(UUID id, UUID userId, UUID tenantId) {
        Membership m = Membership.builder()
                .id(id)
                .userId(userId)
                .tenantId(tenantId)
                .status("ACTIVE")
                .isOwner(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        m.markNotNew();
        return m;
    }

    private Tenant buildTenant(UUID id, String name, String slug) {
        return Tenant.builder()
                .id(id)
                .name(name)
                .slug(slug)
                .tenantType("CLIENT")
                .status("ACTIVE")
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("Should return all non-deleted memberships")
        void shouldReturnNonDeletedMemberships() {
            UUID m1Id = randomId();
            UUID m2Id = randomId();
            Membership active = buildMembership(m1Id, randomId(), TEST_TENANT_ID);
            Membership deleted = buildMembership(m2Id, randomId(), TEST_TENANT_ID);
            deleted.setStatus("DELETED");

            when(membershipRepository.findAll()).thenReturn(List.of(active, deleted));
            when(membershipRoleRepository.findByMembershipId(m1Id)).thenReturn(List.of());

            List<MembershipResponse> result = service.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(m1Id);
            assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("Should return empty list when no memberships")
        void shouldReturnEmptyList() {
            when(membershipRepository.findAll()).thenReturn(List.of());

            assertThat(service.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByTenantId")
    class FindByTenantId {

        @Test
        @DisplayName("Should return active memberships for tenant")
        void shouldReturnMembershipsForTenant() {
            UUID mId = randomId();
            Membership membership = buildMembership(mId, randomId(), TEST_TENANT_ID);
            when(membershipRepository.findActiveByTenantId(TEST_TENANT_ID)).thenReturn(List.of(membership));
            when(membershipRoleRepository.findByMembershipId(mId))
                    .thenReturn(List.of(new MembershipRole(mId, ROLE_ADMIN_ID)));

            List<MembershipResponse> result = service.findByTenantId(TEST_TENANT_ID);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRoleIds()).containsExactly(ROLE_ADMIN_ID);
            verify(membershipRepository).findActiveByTenantId(TEST_TENANT_ID);
        }
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("Should return memberships with local tenant info")
        void shouldReturnMembershipsWithTenantInfo() {
            UUID mId = randomId();
            UUID tenantId = randomId();
            UUID userId = randomId();

            Membership membership = buildMembership(mId, userId, tenantId);
            Tenant tenant = buildTenant(tenantId, "Acme Corp", "acme-corp");

            when(membershipRepository.findActiveByUserId(userId)).thenReturn(List.of(membership));
            when(tenantRepository.findAllByIdIn(List.of(tenantId))).thenReturn(List.of(tenant));
            when(membershipRoleRepository.findByMembershipId(mId)).thenReturn(List.of());

            List<MembershipResponse> result = service.findByUserId(userId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTenant()).isNotNull();
            assertThat(result.get(0).getTenant().getName()).isEqualTo("Acme Corp");
            assertThat(result.get(0).getTenant().getSlug()).isEqualTo("acme-corp");
            assertThat(result.get(0).getTenant().getTenantType()).isEqualTo("CLIENT");
        }

        @Test
        @DisplayName("Should handle tenant not found in map gracefully")
        void shouldHandleMissingTenant() {
            UUID mId = randomId();
            UUID tenantId = randomId();
            UUID userId = randomId();

            Membership membership = buildMembership(mId, userId, tenantId);

            when(membershipRepository.findActiveByUserId(userId)).thenReturn(List.of(membership));
            when(tenantRepository.findAllByIdIn(List.of(tenantId))).thenReturn(List.of());
            when(membershipRoleRepository.findByMembershipId(mId)).thenReturn(List.of());

            List<MembershipResponse> result = service.findByUserId(userId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTenant()).isNull();
        }

        @Test
        @DisplayName("Should batch-fetch tenants for multiple memberships")
        void shouldBatchFetchTenants() {
            UUID userId = randomId();
            UUID t1Id = randomId();
            UUID t2Id = randomId();
            UUID m1Id = randomId();
            UUID m2Id = randomId();

            Membership m1 = buildMembership(m1Id, userId, t1Id);
            Membership m2 = buildMembership(m2Id, userId, t2Id);
            Tenant t1 = buildTenant(t1Id, "Tenant One", "tenant-one");
            Tenant t2 = buildTenant(t2Id, "Tenant Two", "tenant-two");

            when(membershipRepository.findActiveByUserId(userId)).thenReturn(List.of(m1, m2));
            when(tenantRepository.findAllByIdIn(anyList())).thenReturn(List.of(t1, t2));
            when(membershipRoleRepository.findByMembershipId(any())).thenReturn(List.of());

            List<MembershipResponse> result = service.findByUserId(userId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTenant().getName()).isEqualTo("Tenant One");
            assertThat(result.get(1).getTenant().getName()).isEqualTo("Tenant Two");

            verify(tenantRepository, times(1)).findAllByIdIn(anyList());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Should return membership when found")
        void shouldReturnMembership() {
            UUID mId = randomId();
            Membership membership = buildMembership(mId, randomId(), TEST_TENANT_ID);
            when(membershipRepository.findById(mId)).thenReturn(Optional.of(membership));
            when(membershipRoleRepository.findByMembershipId(mId))
                    .thenReturn(List.of(new MembershipRole(mId, ROLE_ADMIN_ID)));

            MembershipResponse result = service.findById(mId);

            assertThat(result.getId()).isEqualTo(mId);
            assertThat(result.getRoleIds()).containsExactly(ROLE_ADMIN_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            UUID mId = randomId();
            when(membershipRepository.findById(mId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(mId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Membership not found");
        }
    }

    @Nested
    @DisplayName("addMember")
    class AddMember {

        @Test
        @DisplayName("Should create membership with roles")
        void shouldCreateMembershipWithRoles() {
            UUID userId = randomId();
            MembershipRequest request = MembershipRequest.builder()
                    .userId(userId)
                    .roleIds(List.of(ROLE_ADMIN_ID, ROLE_USER_ID))
                    .build();

            when(membershipRepository.existsByUserIdAndTenantId(userId, TEST_TENANT_ID)).thenReturn(false);
            when(membershipRepository.save(any(Membership.class))).thenAnswer(inv -> inv.getArgument(0));
            when(membershipRoleRepository.findByMembershipId(any())).thenReturn(List.of());

            MembershipResponse result = service.addMember(TEST_TENANT_ID, request);

            ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
            verify(membershipRepository).save(captor.capture());
            Membership saved = captor.getValue();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getUserId()).isEqualTo(userId);
            assertThat(saved.getTenantId()).isEqualTo(TEST_TENANT_ID);
            assertThat(saved.getStatus()).isEqualTo("ACTIVE");
            assertThat(saved.isNew()).isTrue();

            verify(membershipRoleRepository).insertMembershipRole(any(), eq(ROLE_ADMIN_ID));
            verify(membershipRoleRepository).insertMembershipRole(any(), eq(ROLE_USER_ID));
            verify(eventPublisher).publishMembershipCreated(any(), eq(userId), eq(TEST_TENANT_ID));
        }

        @Test
        @DisplayName("Should create membership without roles when roleIds is null")
        void shouldCreateMembershipWithoutRoles() {
            UUID userId = randomId();
            MembershipRequest request = MembershipRequest.builder()
                    .userId(userId)
                    .roleIds(null)
                    .build();

            when(membershipRepository.existsByUserIdAndTenantId(userId, TEST_TENANT_ID)).thenReturn(false);
            when(membershipRepository.save(any(Membership.class))).thenAnswer(inv -> inv.getArgument(0));
            when(membershipRoleRepository.findByMembershipId(any())).thenReturn(List.of());

            service.addMember(TEST_TENANT_ID, request);

            verify(membershipRoleRepository, never()).insertMembershipRole(any(), any());
        }

        @Test
        @DisplayName("Should throw BusinessException when user already member")
        void shouldThrowWhenDuplicate() {
            UUID userId = randomId();
            MembershipRequest request = MembershipRequest.builder()
                    .userId(userId)
                    .roleIds(List.of(ROLE_ADMIN_ID))
                    .build();

            when(membershipRepository.existsByUserIdAndTenantId(userId, TEST_TENANT_ID)).thenReturn(true);

            assertThatThrownBy(() -> service.addMember(TEST_TENANT_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already a member");

            verify(membershipRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeMember")
    class RemoveMember {

        @Test
        @DisplayName("Should soft-delete membership")
        void shouldSoftDelete() {
            UUID userId = randomId();
            UUID mId = randomId();
            Membership membership = buildMembership(mId, userId, TEST_TENANT_ID);

            when(membershipRepository.findByUserIdAndTenantId(userId, TEST_TENANT_ID))
                    .thenReturn(Optional.of(membership));
            when(membershipRepository.save(any(Membership.class))).thenAnswer(inv -> inv.getArgument(0));

            service.removeMember(TEST_TENANT_ID, userId);

            ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
            verify(membershipRepository).save(captor.capture());
            Membership saved = captor.getValue();

            assertThat(saved.getStatus()).isEqualTo("DELETED");
            assertThat(saved.getDeletedAt()).isNotNull();
            assertThat(saved.getDeletedBy()).isEqualTo(TEST_USER_ID);
            assertThat(saved.isNew()).isFalse();

            verify(eventPublisher).publishMembershipDeleted(mId, userId, TEST_TENANT_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when membership not found")
        void shouldThrowWhenNotFound() {
            UUID userId = randomId();
            when(membershipRepository.findByUserIdAndTenantId(userId, TEST_TENANT_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.removeMember(TEST_TENANT_ID, userId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(membershipRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateRoles")
    class UpdateRoles {

        @Test
        @DisplayName("Should replace all roles for membership")
        void shouldReplaceRoles() {
            UUID mId = randomId();
            UUID userId = randomId();
            Membership membership = buildMembership(mId, userId, TEST_TENANT_ID);

            when(membershipRepository.findById(mId)).thenReturn(Optional.of(membership));
            when(membershipRepository.save(any(Membership.class))).thenAnswer(inv -> inv.getArgument(0));
            when(membershipRoleRepository.findByMembershipId(mId)).thenReturn(List.of());

            service.updateRoles(mId, List.of(ROLE_ADMIN_ID, ROLE_USER_ID));

            verify(membershipRoleRepository).deleteByMembershipId(mId);
            verify(membershipRoleRepository).insertMembershipRole(mId, ROLE_ADMIN_ID);
            verify(membershipRoleRepository).insertMembershipRole(mId, ROLE_USER_ID);
            verify(eventPublisher).publishMembershipRolesUpdated(mId, userId, TEST_TENANT_ID);
        }

        @Test
        @DisplayName("Should clear all roles when null list provided")
        void shouldClearRolesWhenNull() {
            UUID mId = randomId();
            Membership membership = buildMembership(mId, randomId(), TEST_TENANT_ID);

            when(membershipRepository.findById(mId)).thenReturn(Optional.of(membership));
            when(membershipRepository.save(any(Membership.class))).thenAnswer(inv -> inv.getArgument(0));
            when(membershipRoleRepository.findByMembershipId(mId)).thenReturn(List.of());

            service.updateRoles(mId, null);

            verify(membershipRoleRepository).deleteByMembershipId(mId);
            verify(membershipRoleRepository, never()).insertMembershipRole(any(), any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when membership not found")
        void shouldThrowWhenNotFound() {
            UUID mId = randomId();
            when(membershipRepository.findById(mId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateRoles(mId, List.of(ROLE_ADMIN_ID)))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(membershipRoleRepository, never()).deleteByMembershipId(any());
        }
    }
}
