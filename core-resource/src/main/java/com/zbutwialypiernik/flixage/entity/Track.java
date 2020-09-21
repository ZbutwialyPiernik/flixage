package com.zbutwialypiernik.flixage.entity;

import com.zbutwialypiernik.flixage.entity.file.AudioFileEntity;
import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Track extends Queryable {

    @Column
    @Enumerated(EnumType.STRING)
    private MusicGenre genre;

    @ManyToOne
    private Album album;

    @ManyToOne(optional = false)
    private Artist artist;

    @Setter(AccessLevel.NONE)
    @Basic(fetch=FetchType.LAZY)
    @Transient
    @Formula("(SELECT SUM(track_stream.stream_count) FROM track_stream WHERE track_stream.track_id = id)")
    private long streamCount;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private AudioFileEntity audioFile;

}
