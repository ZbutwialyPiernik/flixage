package com.zbutwialypiernik.flixage.repository;


import com.zbutwialypiernik.flixage.entity.Playlist;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends QueryableRepository<Playlist> {

    List<Playlist> findByOwnerId(String id);

}
