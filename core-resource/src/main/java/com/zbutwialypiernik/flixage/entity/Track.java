package com.zbutwialypiernik.flixage.entity;

import com.zbutwialypiernik.flixage.entity.file.AudioFileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne
    private Artist artist;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private AudioFileEntity audioFile;

    private long playCount;

}
