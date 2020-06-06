package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.service.TrackService;
import com.zbutwialypiernik.flixage.ui.component.crud.Crud;
import com.zbutwialypiernik.flixage.ui.component.crud.DtoFormDialog;
import com.zbutwialypiernik.flixage.ui.component.crud.SingleCallbackDataProvider;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.FormBuilder;
import com.zbutwialypiernik.flixage.ui.component.form.dto.TrackForm;

import java.util.List;


public class TrackCrud extends Crud<Track, TrackForm> {

    // Null if track is Single
    private final TrackService trackService;
    private Album parentAlbum;
    private Artist artist;

    private List<Track> items;

    public TrackCrud(TrackService trackService, Artist artist, Album parentAlbum, MapperFactory factory) {
        super(Track.class);
        this.artist = artist;
        this.trackService = trackService;
        this.parentAlbum = parentAlbum;

        setForm(createForm("Artist"), factory.createMapper());
        setService(trackService);

        getGrid().setDataProvider(new SingleCallbackDataProvider<>(() -> {
            if (parentAlbum == null) {
                return trackService.getArtistSingles(artist.getId());
            } else {
                return trackService.getTracksByAlbumId(parentAlbum.getId());
            }
        } ));

        FormBuilder<TrackForm> formBuilder = new FormBuilder<>(TrackForm.class);
        formBuilder.setHeader("Add song to album!");
        formBuilder.addFields(new FormBuilder.FormField("audioResource", "Track file"));
        formBuilder.addFields(new FormBuilder.FormField("genre", "Genre"));

        setForm(formBuilder.build(), factory.createMapper());

        addColumn(Track::getGenre).setHeader("Genre");
        addComponentColumn(track -> {
            var checkbox = new Checkbox();
            checkbox.setValue(track.getAudioFile() != null);
            checkbox.setReadOnly(true);
            return checkbox;
        }).setHeader("Audio file");
    }

    @Override
    protected void onCreate(DtoFormDialog<Track, TrackForm>.SubmitEvent event) {
        event.getEntity().setArtist(artist);
        event.getEntity().setAlbum(parentAlbum);  // Can be null when track is single

        Track entity = trackService.create(event.getEntity());

        if (event.getDto().getThumbnailResource() != null) {
            trackService.saveThumbnail(entity, event.getDto().getThumbnailResource());
        }

        if (event.getDto().getAudioResource() != null) {
            trackService.saveTrackFile(entity, event.getDto().getAudioResource());
        }

        formDialog.getContent().close();
        refresh();
    }

    @Override
    protected void onUpdate(DtoFormDialog<Track, TrackForm>.SubmitEvent event) {
        Track entity = trackService.update(event.getEntity());

        if (event.getDto().getThumbnailResource() != null) {
            trackService.saveThumbnail(entity, event.getDto().getThumbnailResource());
        }

        if (event.getDto().getAudioResource() != null) {
            trackService.saveTrackFile(entity, event.getDto().getAudioResource());
        }

        formDialog.getContent().close();
        refresh();
    }

    public Form<TrackForm> createForm(String header) {
        var generator = new FormBuilder<>(TrackForm.class);
        generator.setHeader(header);
        generator.addFields(new FormBuilder.FormField("name", "Name"));
        generator.addFields(new FormBuilder.FormField("genre", "Genrce"));
        generator.addFields(new FormBuilder.FormField("audioResource", "Track"));
        generator.addFields(new FormBuilder.FormField("thumbnailResource", "Thumbnail"));

        return generator.build();
    }

}
