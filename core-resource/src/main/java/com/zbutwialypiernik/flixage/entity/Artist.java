package com.zbutwialypiernik.flixage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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

}
