package com.poc.tenant.repository;

import com.poc.tenant.domain.Customer;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    @Query("SELECT * FROM TNT_CORE_CUSTOMERS WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<Customer> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_CORE_CUSTOMERS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<Customer> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_CORE_CUSTOMERS WHERE EMAIL = :email AND TENANT_ID = :tenantId")
    Optional<Customer> findByEmailAndTenantId(@Param("email") String email, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_CORE_CUSTOMERS WHERE STATUS = :status AND TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<Customer> findByStatusAndTenantId(@Param("status") String status, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_CORE_CUSTOMERS WHERE TENANT_ID = :tenantId AND (LOWER(NAME) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(EMAIL) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(COMPANY) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY NAME")
    List<Customer> searchByTenantId(@Param("tenantId") UUID tenantId, @Param("query") String query);

    @Query("SELECT * FROM TNT_CORE_CUSTOMERS WHERE TENANT_ID = :tenantId AND USER_ID = :userId")
    Optional<Customer> findByTenantIdAndUserId(@Param("tenantId") UUID tenantId, @Param("userId") UUID userId);

    @Query("SELECT * FROM TNT_CORE_CUSTOMERS WHERE USER_ID = :userId")
    List<Customer> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT * FROM TNT_CORE_CUSTOMERS WHERE TENANT_ID = :tenantId AND USER_ID IS NOT NULL ORDER BY NAME")
    List<Customer> findWithAccountsByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_CORE_CUSTOMERS WHERE TENANT_ID = :tenantId AND EMAIL = :email")
    Optional<Customer> findByTenantIdAndEmail(@Param("tenantId") UUID tenantId, @Param("email") String email);
}
