package com.poc.tenant.membership.repository;

import com.poc.tenant.BaseIntegrationTest;
import com.poc.tenant.membership.domain.Membership;
import com.poc.tenant.membership.domain.MembershipRole;
import com.poc.tenant.tenant.domain.Tenant;
import com.poc.tenant.tenant.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Membership Repository Integration Tests (H2 + Liquibase)")
class MembershipRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipRoleRepository membershipRoleRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private UUID tenantId;
    private UUID userId1;
    private UUID userId2;
    private UUID roleAdminId;
    private UUID roleUserId;

    @BeforeEach
    void setUp() {
        tenantId = randomId();
        userId1 = randomId();
        userId2 = randomId();
        roleAdminId = randomId();
        roleUserId = randomId();

        // Create tenant (needed for findByUserId with tenant enrichment)
        tenantRepository.save(Tenant.builder()
                .id(tenantId)
                .name("Test Tenant")
                .slug("test-tenant-" + tenantId.toString().substring(0, 8))
                .tenantType("CLIENT")
                .status("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());
    }

    private Membership saveMembership(UUID userId, UUID tenantId, String status) {
        return membershipRepository.save(Membership.builder()
                .id(randomId())
                .userId(userId)
                .tenantId(tenantId)
                .status(status)
                .isOwner(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());
    }

    @Nested
    @DisplayName("Liquibase Schema Validation")
    class SchemaValidation {

        @Test
        @DisplayName("Should create TNT_ACC_MEMBERSHIPS table via Liquibase")
        void shouldCreateMembershipsTable() {
            Membership membership = saveMembership(userId1, tenantId, "ACTIVE");

            Optional<Membership> found = membershipRepository.findById(membership.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getUserId()).isEqualTo(userId1);
            assertThat(found.get().getTenantId()).isEqualTo(tenantId);
            assertThat(found.get().getStatus()).isEqualTo("ACTIVE");
            assertThat(found.get().getIsOwner()).isFalse();
        }

        @Test
        @DisplayName("Should create TNT_ACC_MEMBERSHIP_ROLES table via Liquibase")
        void shouldCreateMembershipRolesTable() {
            Membership membership = saveMembership(userId1, tenantId, "ACTIVE");

            membershipRoleRepository.insertMembershipRole(membership.getId(), roleAdminId);

            List<MembershipRole> roles = membershipRoleRepository.findByMembershipId(membership.getId());
            assertThat(roles).hasSize(1);
            assertThat(roles.get(0).getRoleId()).isEqualTo(roleAdminId);
        }
    }

    @Nested
    @DisplayName("MembershipRepository Queries")
    class MembershipQueries {

        @Test
        @DisplayName("findActiveByUserId should return only active non-deleted memberships")
        void findActiveByUserId() {
            saveMembership(userId1, tenantId, "ACTIVE");

            UUID otherTenantId = randomId();
            tenantRepository.save(Tenant.builder()
                    .id(otherTenantId).name("Other").slug("other-" + otherTenantId.toString().substring(0, 8))
                    .tenantType("CLIENT").status("ACTIVE")
                    .createdAt(Instant.now()).updatedAt(Instant.now()).build());
            saveMembership(userId1, otherTenantId, "ACTIVE");

            // Deleted membership (soft-deleted)
            Membership deleted = saveMembership(userId1, randomId(), "ACTIVE");
            deleted.softDelete(TEST_USER_ID);
            deleted.markNotNew();
            membershipRepository.save(deleted);

            List<Membership> result = membershipRepository.findActiveByUserId(userId1);
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(m -> "ACTIVE".equals(m.getStatus()));
            assertThat(result).allMatch(m -> m.getDeletedAt() == null);
        }

        @Test
        @DisplayName("findActiveByTenantId should return only active memberships for tenant")
        void findActiveByTenantId() {
            saveMembership(userId1, tenantId, "ACTIVE");
            saveMembership(userId2, tenantId, "ACTIVE");

            // Inactive membership
            saveMembership(randomId(), tenantId, "INACTIVE");

            List<Membership> result = membershipRepository.findActiveByTenantId(tenantId);
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("findByUserIdAndTenantId should return exact match")
        void findByUserIdAndTenantId() {
            saveMembership(userId1, tenantId, "ACTIVE");

            Optional<Membership> result = membershipRepository.findByUserIdAndTenantId(userId1, tenantId);
            assertThat(result).isPresent();
            assertThat(result.get().getUserId()).isEqualTo(userId1);
            assertThat(result.get().getTenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("findByUserIdAndTenantId should return empty when no match")
        void findByUserIdAndTenantIdEmpty() {
            Optional<Membership> result = membershipRepository.findByUserIdAndTenantId(randomId(), tenantId);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("existsByUserIdAndTenantId should detect duplicates")
        void existsByUserIdAndTenantId() {
            saveMembership(userId1, tenantId, "ACTIVE");

            assertThat(membershipRepository.existsByUserIdAndTenantId(userId1, tenantId)).isTrue();
            assertThat(membershipRepository.existsByUserIdAndTenantId(randomId(), tenantId)).isFalse();
        }

        @Test
        @DisplayName("updateStatus should change membership status")
        void updateStatus() {
            Membership membership = saveMembership(userId1, tenantId, "ACTIVE");

            membershipRepository.updateStatus(membership.getId(), "SUSPENDED");

            Optional<Membership> found = membershipRepository.findById(membership.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo("SUSPENDED");
        }

        @Test
        @DisplayName("Soft delete should set deletedAt and status DELETED")
        void softDelete() {
            Membership membership = saveMembership(userId1, tenantId, "ACTIVE");
            membership.softDelete(TEST_USER_ID);
            membership.markNotNew();
            membershipRepository.save(membership);

            Optional<Membership> found = membershipRepository.findById(membership.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo("DELETED");
            assertThat(found.get().getDeletedAt()).isNotNull();
            assertThat(found.get().getDeletedBy()).isEqualTo(TEST_USER_ID);

            // Should NOT appear in active queries
            List<Membership> active = membershipRepository.findActiveByUserId(userId1);
            assertThat(active).isEmpty();
        }
    }

    @Nested
    @DisplayName("MembershipRoleRepository Queries")
    class MembershipRoleQueries {

        @Test
        @DisplayName("insertMembershipRole should create role association")
        void insertMembershipRole() {
            Membership membership = saveMembership(userId1, tenantId, "ACTIVE");

            membershipRoleRepository.insertMembershipRole(membership.getId(), roleAdminId);
            membershipRoleRepository.insertMembershipRole(membership.getId(), roleUserId);

            List<MembershipRole> roles = membershipRoleRepository.findByMembershipId(membership.getId());
            assertThat(roles).hasSize(2);
            assertThat(roles).extracting(MembershipRole::getRoleId)
                    .containsExactlyInAnyOrder(roleAdminId, roleUserId);
        }

        @Test
        @DisplayName("deleteByMembershipId should remove all roles for membership")
        void deleteByMembershipId() {
            Membership membership = saveMembership(userId1, tenantId, "ACTIVE");

            membershipRoleRepository.insertMembershipRole(membership.getId(), roleAdminId);
            membershipRoleRepository.insertMembershipRole(membership.getId(), roleUserId);

            membershipRoleRepository.deleteByMembershipId(membership.getId());

            List<MembershipRole> roles = membershipRoleRepository.findByMembershipId(membership.getId());
            assertThat(roles).isEmpty();
        }

        @Test
        @DisplayName("FK cascade should delete roles when membership is deleted")
        void fkCascade() {
            Membership membership = saveMembership(userId1, tenantId, "ACTIVE");
            membershipRoleRepository.insertMembershipRole(membership.getId(), roleAdminId);

            membershipRepository.deleteById(membership.getId());

            List<MembershipRole> roles = membershipRoleRepository.findByMembershipId(membership.getId());
            assertThat(roles).isEmpty();
        }

        @Test
        @DisplayName("findByMembershipId should return empty for unknown membership")
        void findByMembershipIdEmpty() {
            List<MembershipRole> roles = membershipRoleRepository.findByMembershipId(randomId());
            assertThat(roles).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tenant Enrichment (LOCAL)")
    class TenantEnrichment {

        @Test
        @DisplayName("TenantRepository.findAllByIdIn should batch-fetch tenants")
        void findAllByIdIn() {
            UUID t2Id = randomId();
            tenantRepository.save(Tenant.builder()
                    .id(t2Id).name("Second Tenant").slug("second-" + t2Id.toString().substring(0, 8))
                    .tenantType("RESELLER").status("ACTIVE")
                    .createdAt(Instant.now()).updatedAt(Instant.now()).build());

            List<Tenant> tenants = tenantRepository.findAllByIdIn(List.of(tenantId, t2Id));
            assertThat(tenants).hasSize(2);
            assertThat(tenants).extracting(Tenant::getName)
                    .containsExactlyInAnyOrder("Test Tenant", "Second Tenant");
        }
    }
}
