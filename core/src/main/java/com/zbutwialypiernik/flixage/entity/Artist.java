package com.zbutwialypiernik.flixage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Artist extends Queryable {

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Album> albums = new ArrayList<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Track> singles = new ArrayList<>();

    @Lob
    private byte[] artistAvatar;

}
