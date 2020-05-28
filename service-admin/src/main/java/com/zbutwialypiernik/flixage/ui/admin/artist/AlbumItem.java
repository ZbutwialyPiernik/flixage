package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.details.Details;
import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Artist;

public class AlbumItem extends Details {

    private Album album;

    public AlbumItem(Album album) {
        this.album = album;
    }



}
