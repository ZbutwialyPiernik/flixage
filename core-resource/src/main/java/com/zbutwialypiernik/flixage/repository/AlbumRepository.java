package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Album;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AlbumRepository extends QueryableRepository<Album> {

    List<Album> findByArtistId(String id);

}
