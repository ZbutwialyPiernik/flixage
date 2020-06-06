package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, String> {

    long countByUserId(String id);
}
