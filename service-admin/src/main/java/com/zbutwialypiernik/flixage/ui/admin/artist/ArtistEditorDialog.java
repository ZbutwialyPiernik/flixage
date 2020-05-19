package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.ui.admin.artist.form.TrackFormDTO;
import com.zbutwialypiernik.flixage.ui.component.crud.DtoFormDialog;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import com.zbutwialypiernik.flixage.ui.component.form.FormBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

public class ArtistEditorDialog extends Dialog {

    private final Image artistAvatar;

    private final Text artistName;

    public ListPanel<Track> songList;

    private final ArtistService artistService;

    @Autowired
    public ArtistEditorDialog(ArtistService artistService, MapperFactory mapperFactory) {
        this.artistService = artistService;

        artistName = new Text("");
        artistAvatar = new Image();

        FormBuilder<TrackFormDTO> songFormBuilder = new FormBuilder<>(TrackFormDTO.class);
        songFormBuilder.setHeader("Add song to album!");
        songFormBuilder.addFields(new FormBuilder.FormField("name", "Name"));

        DtoFormDialog<Track, TrackFormDTO> songFormDialog = new DtoFormDialog<>(
                Track.class,
                songFormBuilder.build(),
                mapperFactory.createConverter());

        songList = new ListPanel<>(songFormDialog);
        songList.setRenderer(new ComponentRenderer<>() {
            @Override
            public com.vaadin.flow.component.Component createComponent(Track track) {
                return new HorizontalLayout(
                        //ComponentUtils.imageFromByteArray(album.get(), album.getId(), "album logo#" + album.getId()),
                        new Text(track.getName()));
            }
        });

        add(new HorizontalLayout(artistAvatar, artistName));
        add(new HorizontalLayout(songList)); //, panel("singiels")));
    }

    private void bindArtist(Artist artist) {
        artistName.setText(artist.getName());

        artistService.getThumbnailById(artist.getId()).ifPresentOrElse((array) -> {
            artistAvatar.setSrc(new StreamResource(artist.getId(), () -> new ByteArrayInputStream(array)));
        }, () -> {
            artistAvatar.setSrc("https://www.theatromarrakech.com/wp-content/plugins/urvenue-plugin/images/placeholder.artist.jpg");
        });

        artistAvatar.setAlt("artist avatar#" + artist.getId());
        artistAvatar.setWidth("96px");
        artistAvatar.setWidth("96px");

        songList.setItems();
    }

    public void setArtist(Artist artist) {
        this.bindArtist(artist);
    }

}
