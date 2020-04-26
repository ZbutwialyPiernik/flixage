package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Queryable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface QueryableRepository<T extends Queryable> extends JpaRepository<T, String> {

    Page<T> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
