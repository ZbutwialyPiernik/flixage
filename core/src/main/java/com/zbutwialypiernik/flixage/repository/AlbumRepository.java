package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends QueryableRepository<Album> {

    List<Album> findAlbumsByArtistId(String id);

}
