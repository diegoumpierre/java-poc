package com.poc.chat.repository;

import com.poc.chat.domain.ChatUser;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatUserRepository extends CrudRepository<ChatUser, Long>, ChatUserRepositoryCustom {

    Optional<ChatUser> findByExternalUserIdAndTenantId(UUID externalUserId, UUID tenantId);

    List<ChatUser> findByTenantId(UUID tenantId);

    @Query("SELECT * FROM CHAT_USER WHERE TENANT_ID = :tenantId AND ID != :excludeId ORDER BY NAME")
    List<ChatUser> findByTenantIdExcludingUser(@Param("tenantId") UUID tenantId, @Param("excludeId") Long excludeId);

    @Modifying
    @Query("UPDATE CHAT_USER SET STATUS = :status, LAST_SEEN_AT = :lastSeenAt, UPDATED_AT = :now WHERE ID = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status,
                      @Param("lastSeenAt") Instant lastSeenAt, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE CHAT_USER SET NAME = :name, EMAIL = :email, AVATAR_URL = :avatarUrl, UPDATED_AT = :now WHERE ID = :id")
    void updateProfile(@Param("id") Long id, @Param("name") String name,
                       @Param("email") String email, @Param("avatarUrl") String avatarUrl, @Param("now") Instant now);

    @Query("SELECT * FROM CHAT_USER WHERE LOWER(NAME) = LOWER(:name) AND TENANT_ID = :tenantId LIMIT 1")
    Optional<ChatUser> findByNameIgnoreCaseAndTenantId(@Param("name") String name, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM CHAT_USER WHERE TENANT_ID = :tenantId AND LOWER(NAME) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY NAME LIMIT 10")
    List<ChatUser> searchByNamePrefix(@Param("tenantId") UUID tenantId, @Param("prefix") String prefix);
}
