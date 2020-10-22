package com.zbutwialypiernik.flixage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Playlist extends Queryable {

    public static final int SHARE_CODE_LENGTH = 6;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Track> tracks = new ArrayList<>();

    @ManyToOne(optional = false)
    private User owner;

    @Column(length = SHARE_CODE_LENGTH, updatable = false, unique = true)
    private String shareCode;

}
