package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, String> {

    long countByUserId(String id);

    @Query("DELETE FROM RefreshToken t" +
            "ORDER BY t.creationTime ASC " +
            "LIMIT 1;" +
            "WHERE t.user.id = :userId")
    void deleteOldestToken(String userId);

}
