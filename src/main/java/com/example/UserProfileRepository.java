package com.example;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserProfileRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserProfile> rowMapper = new RowMapper<>() {
        @Override
        public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserProfile(
                    rs.getString("CONVERSATION_ID"),
                    rs.getString("SUMMARY"),
                    rs.getTimestamp("UPDATED_AT").toLocalDateTime());
        }
    };

    public UserProfileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserProfile> findByConversationId(String conversationId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT CONVERSATION_ID, SUMMARY, UPDATED_AT FROM USER_PROFILE WHERE CONVERSATION_ID = ?",
                    rowMapper,
                    conversationId));
        }
        catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<String> findAllConversationIds() {
        return jdbcTemplate.query(
                "SELECT DISTINCT CONVERSATION_ID FROM SPRING_AI_CHAT_MEMORY",
                (rs, rowNum) -> rs.getString("CONVERSATION_ID"));
    }

    public void save(UserProfile profile) {
        int updated = jdbcTemplate.update(
                "UPDATE USER_PROFILE SET SUMMARY = ?, UPDATED_AT = ? WHERE CONVERSATION_ID = ?",
                profile.summary(),
                java.sql.Timestamp.valueOf(profile.updatedAt()),
                profile.conversationId());

        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO USER_PROFILE (CONVERSATION_ID, SUMMARY, UPDATED_AT) VALUES (?, ?, ?)",
                    profile.conversationId(),
                    profile.summary(),
                    java.sql.Timestamp.valueOf(profile.updatedAt()));
        }
    }
}
