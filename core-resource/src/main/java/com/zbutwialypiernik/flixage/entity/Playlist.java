package com.zbutwialypiernik.flixage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Playlist extends Queryable {

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Track> tracks = new ArrayList<>();

    @ManyToOne
    private User owner;

}
