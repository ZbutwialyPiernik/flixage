package com.zbutwialypiernik.flixage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Track extends Queryable {

    @Column
    private MusicGenre genre;

    @ManyToOne
    private Album album;

    @ManyToOne
    private Artist artist;

    @ContentId
    @Column
    private String fileId;

    @ContentLength
    @Column
    private long fileSize;

}
