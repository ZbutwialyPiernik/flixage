package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.service.AlbumService;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.service.TrackService;
import com.zbutwialypiernik.flixage.ui.admin.AdminPanelView;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = Routes.ARTISTS, layout = AdminPanelView.class)
public class ArtistEditorPage extends VerticalLayout implements HasUrlParameter<String> {

    // Services
    private final ArtistService artistService;
    private final TrackService trackService;
    private final AlbumService albumService;

    private final MapperFactory factory;

    // UI
    private Image artistAvatar;
    private Label artistName;

    private TrackCrud trackCrud;
    private AlbumCrud albumCrud;

    private Artist artist;

    private boolean isInitialized = false;

    @Autowired
    public ArtistEditorPage(ArtistService artistService, TrackService trackService, AlbumService albumService, MapperFactory factory) {
        this.factory = factory;
        this.artistService = artistService;
        this.trackService = trackService;
        this.albumService = albumService;
    }

    private void init() {
        if (!isInitialized) {
            artistName = new Label();
            artistAvatar = new Image();

            albumCrud = new AlbumCrud(albumService, artist, factory);
            trackCrud = new TrackCrud(trackService, artist, null, factory);

            albumCrud.setPadding(false);
            trackCrud.setPadding(false);

            var singlesLabel = new HorizontalLayout(new Label("Singles"));
            singlesLabel.setWidthFull();

            var singlesLayout = new VerticalLayout(singlesLabel, trackCrud);
            singlesLayout.setWidthFull();

            var albumsLabel = new HorizontalLayout(new Label("Albums"));
            singlesLabel.setWidthFull();
            singlesLabel.setVerticalComponentAlignment(Alignment.CENTER);

            var albumsLayout = new VerticalLayout(albumsLabel, albumCrud);
            albumsLayout.setWidthFull();

            var artistLabel = new HorizontalLayout(artistAvatar, artistName);
            albumsLayout.setWidthFull();

            var crudLayout = new HorizontalLayout(singlesLayout, albumsLayout);
            crudLayout.setWidthFull();

            add(artistLabel, crudLayout);

            isInitialized = true;
        }

        artistName.setText(artist.getName());

        artistService.getThumbnailById(artist.getId()).ifPresentOrElse(
                resource -> artistAvatar.setSrc(new StreamResource(artist.getId(), resource::getInputStream)),
                () -> artistAvatar.setSrc("https://www.theatromarrakech.com/wp-content/plugins/urvenue-plugin/images/placeholder.artist.jpg"));

        artistAvatar.setAlt("artist avatar#" + artist.getId());
        artistAvatar.setWidth("96px");
        artistAvatar.setWidth("96px");

        albumCrud.refresh();
        trackCrud.refresh();
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        Optional<Artist> artistOptional = artistService.findById(parameter);

        if (artistOptional.isPresent()) {
            artist = artistOptional.get();
            init();
        } else {
            event.rerouteTo(Routes.NOT_FOUND);
        }
    }

}
