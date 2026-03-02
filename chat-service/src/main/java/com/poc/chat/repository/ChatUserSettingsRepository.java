package com.poc.chat.repository;

import com.poc.chat.domain.ChatUserSettings;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatUserSettingsRepository extends CrudRepository<ChatUserSettings, Long> {

    @Query("SELECT * FROM CHAT_USER_SETTINGS WHERE USER_ID = :userId")
    Optional<ChatUserSettings> findByUserId(@Param("userId") Long userId);
}
