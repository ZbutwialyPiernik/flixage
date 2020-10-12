package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.TrackStream;
import com.zbutwialypiernik.flixage.entity.TrackStreamId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TrackStreamRepository extends JpaRepository<TrackStream, TrackStreamId> {

    Page<TrackStream> findByIdUserIdOrderByUpdateTimeDesc(String id, Pageable pageable);

    /**
     * Queries most listened artists of user, within selected time period
     *
     * @param userId
     * @param startDate
     * @param endDate
     * @param pageable
     * @return
     */
    @Query(value = "SELECT t.artist FROM TrackStream s " +
            "JOIN s.id.track t " +
            "WHERE s.id.user.id = :userId " +
            "AND " +
            "s.updateTime BETWEEN :startDate AND :endDate " +
            "GROUP BY t.artist " +
            "ORDER BY SUM(s.streamCount)")
    Page<Artist> findMostListenedArtists(String userId, Instant startDate, Instant endDate, Pageable pageable);

}
