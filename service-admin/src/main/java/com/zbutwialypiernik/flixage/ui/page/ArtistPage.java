package com.zbutwialypiernik.flixage.ui.page;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import com.zbutwialypiernik.flixage.ui.component.crud.impl.AlbumCrud;
import com.zbutwialypiernik.flixage.ui.component.crud.impl.TrackCrud;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = Routes.ARTISTS, layout = RootPage.class)
public class ArtistPage extends VerticalLayout implements HasUrlParameter<String> {

    // Services
    private final ArtistService artistService;
    private final TrackService trackService;
    private final AlbumService albumService;

    private final MapperFactory factory;

    @Autowired
    public ArtistPage(ArtistService artistService, TrackService trackService, AlbumService albumService, MapperFactory factory) {
        this.artistService = artistService;
        this.trackService = trackService;
        this.albumService = albumService;
        this.factory = factory;
    }

    private void init(Artist artist) {
        var backButton = new Button(VaadinIcon.ARROW_BACKWARD.create(), event -> {
            //var path = uriFactory.newBuilder().pathSegment(Routes.LIBRARY).toUriString();
            UI.getCurrent().navigate(Routes.ARTISTS);
        });
        var artistName = new Label(artist.getName());
        var artistAvatar = new Image();
        artistAvatar.setAlt("artist avatar#" + artist.getId());
        artistAvatar.setWidth("96px");
        artistAvatar.setWidth("96px");

        artistService.getThumbnailById(artist.getId()).ifPresentOrElse(
                resource -> artistAvatar.setSrc(new StreamResource(artist.getId(), resource::getInputStream)),
                () -> artistAvatar.setSrc("https://www.theatromarrakech.com/wp-content/plugins/urvenue-plugin/images/placeholder.artist.jpg"));

        var albumCrud = new AlbumCrud(albumService, artist, factory);
        var trackCrud = new TrackCrud(trackService, artist, null, factory);

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

        add(backButton, artistLabel, crudLayout);

        albumCrud.refresh();
        trackCrud.refresh();
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        Optional<Artist> artistOptional = artistService.findById(parameter);

        if (artistOptional.isPresent()) {
            init(artistOptional.get());
        } else {
            event.rerouteTo(Routes.NOT_FOUND);
        }
    }

}
