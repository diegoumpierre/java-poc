package com.poc.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BackupCodeRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(UUID userId, String codeHash) {
        String sql = "INSERT INTO AUTH_BACKUP_CODES (ID, USER_ID, CODE_HASH) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, UUID.randomUUID().toString(), userId.toString(), codeHash);
    }

    public int countUnusedByUserId(UUID userId) {
        String sql = "SELECT COUNT(*) FROM AUTH_BACKUP_CODES WHERE USER_ID = ? AND USED_AT IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId.toString());
        return count != null ? count : 0;
    }

    public void deleteByUserId(UUID userId) {
        String sql = "DELETE FROM AUTH_BACKUP_CODES WHERE USER_ID = ?";
        jdbcTemplate.update(sql, userId.toString());
    }

    public boolean verifyAndUseCode(UUID userId, String codeHash) {
        // Find unused backup code
        String findSql = "SELECT ID, CODE_HASH FROM AUTH_BACKUP_CODES WHERE USER_ID = ? AND USED_AT IS NULL";
        var codes = jdbcTemplate.queryForList(findSql, userId.toString());
        
        for (var code : codes) {
            String storedHash = (String) code.get("CODE_HASH");
            // This would need to use PasswordEncoder.matches() in practice
            // For now, direct comparison (caller should pass hashed value)
            if (storedHash.equals(codeHash)) {
                String updateSql = "UPDATE AUTH_BACKUP_CODES SET USED_AT = NOW() WHERE ID = ?";
                jdbcTemplate.update(updateSql, code.get("ID"));
                return true;
            }
        }
        return false;
    }
}
