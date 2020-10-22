package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, String> {

    long countByUserId(String id);

    @Query(nativeQuery = true, value = "DELETE FROM refresh_token " +
            "WHERE user_id = :userId " +
            "ORDER BY refresh_token.creation_time ASC " +
            "LIMIT 1")
    void deleteOldestToken(String userId);

}
