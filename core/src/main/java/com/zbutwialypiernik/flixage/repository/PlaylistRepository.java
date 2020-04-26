package com.zbutwialypiernik.flixage.repository;


import com.zbutwialypiernik.flixage.entity.Playlist;
import org.hibernate.persister.entity.Queryable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends QueryableRepository<Playlist> {

    List<Playlist> findByOwnerId(String id);

}
