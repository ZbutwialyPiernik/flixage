package com.zbutwialypiernik.flixage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@javax.persistence.Entity
public class Track extends Queryable {

    @Column
    private MusicGenre genre;

    @ManyToOne
    private Album album;

    @ManyToOne
    private Artist artist;

}
