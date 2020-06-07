package com.zbutwialypiernik.flixage.ui.page;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.service.AlbumService;
import com.zbutwialypiernik.flixage.service.TrackService;
import com.zbutwialypiernik.flixage.ui.component.crud.impl.TrackCrud;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = Routes.ALBUM, layout = RootPage.class)
public class AlbumPage extends VerticalLayout implements HasUrlParameter<String> {

    // Services
    private final TrackService trackService;
    private final AlbumService albumService;

    private final MapperFactory factory;

    private Album album;

    @Autowired
    public AlbumPage(TrackService trackService, AlbumService albumService, MapperFactory factory) {
        this.factory = factory;
        this.trackService = trackService;
        this.albumService = albumService;
    }

    private void init() {
        var artistName = new Label(album.getName());
        var artistAvatar = new Image();

        artistAvatar.setAlt("album thumbnail#" + album.getId());
        artistAvatar.setWidth("96px");
        artistAvatar.setWidth("96px");
        albumService.getThumbnailById(album.getId()).ifPresentOrElse(
                resource -> artistAvatar.setSrc(new StreamResource(album.getId(), resource::getInputStream)),
                () -> artistAvatar.setSrc("https://www.theatromarrakech.com/wp-content/plugins/urvenue-plugin/images/placeholder.artist.jpg"));

        var trackCrud = new TrackCrud(trackService, album.getArtist(), album, factory);
        trackCrud.setPadding(false);

        var artistLabel = new HorizontalLayout(artistAvatar, artistName);
        artistLabel.setWidthFull();

        add(artistLabel, trackCrud);

        trackCrud.refresh();
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        Optional<Album> albumArtist = albumService.findById(parameter);

        if (albumArtist.isPresent()) {
            album = albumArtist.get();
            init();
        } else {
            event.rerouteTo(Routes.NOT_FOUND);
        }
    }

}

