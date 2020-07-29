package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends QueryableRepository<Artist> {

    Page<Artist> findAllByOrderByCreationTimeDesc(Pageable pageable);

}
