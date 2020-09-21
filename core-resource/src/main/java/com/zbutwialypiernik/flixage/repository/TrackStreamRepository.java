package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.TrackStream;
import com.zbutwialypiernik.flixage.entity.TrackStreamId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackStreamRepository extends JpaRepository<TrackStream, TrackStreamId> {

    Page<TrackStream> findByIdUserIdOrderByUpdateTimeDesc(String id, Pageable pageable);


}
