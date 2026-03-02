package com.poc.chat.repository;

import com.poc.chat.domain.ChatNotification;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatNotificationRepository extends CrudRepository<ChatNotification, Long>, ChatNotificationRepositoryCustom {

    @Query("SELECT COUNT(*) FROM CHAT_NOTIFICATION WHERE USER_ID = :userId AND IS_READ = 0")
    int countUnread(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE CHAT_NOTIFICATION SET IS_READ = 1 WHERE ID = :id AND USER_ID = :userId")
    void markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE CHAT_NOTIFICATION SET IS_READ = 1 WHERE USER_ID = :userId AND IS_READ = 0")
    void markAllAsRead(@Param("userId") Long userId);
}
