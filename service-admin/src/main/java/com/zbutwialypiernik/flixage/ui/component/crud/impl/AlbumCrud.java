package com.zbutwialypiernik.flixage.ui.component.crud.impl;

import com.vaadin.flow.router.RouterLink;
import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.service.AlbumService;
import com.zbutwialypiernik.flixage.ui.page.AlbumPage;
import com.zbutwialypiernik.flixage.ui.component.crud.Crud;
import com.zbutwialypiernik.flixage.ui.component.crud.DtoFormDialog;
import com.zbutwialypiernik.flixage.ui.component.crud.SingleCallbackDataProvider;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import com.zbutwialypiernik.flixage.ui.component.form.FormBuilder;
import com.zbutwialypiernik.flixage.ui.component.form.dto.AlbumForm;

public class AlbumCrud extends Crud<Album, AlbumForm> {

    // Null if track is Single
    private Artist artist;

    public AlbumCrud(AlbumService albumService, Artist artist, MapperFactory factory) {
        super(Album.class);
        this.artist = artist;

        setService(albumService);

        getGrid().setDataProvider(new SingleCallbackDataProvider<>(() -> albumService.getByArtistId(artist.getId())));

        FormBuilder<AlbumForm> formBuilder = new FormBuilder<>(AlbumForm.class);
        formBuilder.setHeader("Album");

        setForm(formBuilder.build(), factory.createMapper());

        addComponentColumn(album -> new RouterLink("Editor", AlbumPage.class, album.getId()));
    }

    @Override
    protected void onCreate(DtoFormDialog<Album, AlbumForm>.SubmitEvent event) {
        event.getEntity().setArtist(artist);
        Album entity = getService().create(event.getEntity());

        if (event.getDto().getThumbnailResource() != null) {
            getService().saveThumbnail(entity, event.getDto().getThumbnailResource());
        }

        formDialog.getContent().close();
        refresh();
    }

}

