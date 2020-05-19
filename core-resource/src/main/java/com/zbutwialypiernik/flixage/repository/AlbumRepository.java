package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
public interface AlbumRepository extends QueryableRepository<Album> {

    Page<Album> findAlbumsByArtistId(String id, Pageable pageable);

}
