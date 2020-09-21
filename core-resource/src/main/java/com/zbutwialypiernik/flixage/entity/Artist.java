package com.zbutwialypiernik.flixage.entity;

import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Artist extends Queryable {

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "artist")
    private List<Album> albums = new ArrayList<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "artist")
    private List<Track> tracks = new ArrayList<>();

    // Unique listeners of last 30 days
    @Setter(AccessLevel.NONE)
    @Basic(fetch= FetchType.LAZY)
    @Transient
    @Formula("(SELECT COUNT(DISTINCT track_stream.user_id) " +
            "FROM track_stream " +
            "INNER JOIN track " +
            "ON track_stream.track_id = track.id " +
            "WHERE track.artist_id = id " +
            "AND " +
            "DATEDIFF(track_stream.update_time, NOW()) BETWEEN 0 AND 30)")
    private long monthlyListeners;

}
