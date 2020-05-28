package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.service.AlbumService;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.service.file.resource.AudioResource;
import com.zbutwialypiernik.flixage.service.file.resource.ImageResource;
import com.zbutwialypiernik.flixage.service.TrackService;
import com.zbutwialypiernik.flixage.ui.admin.artist.form.AlbumFormDTO;
import com.zbutwialypiernik.flixage.ui.admin.artist.form.TrackFormDTO;
import com.zbutwialypiernik.flixage.ui.component.ComponentUtils;
import com.zbutwialypiernik.flixage.ui.component.crud.DtoFormDialog;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.FormBuilder;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

@Route(Routes.ARTISTS)
public class ArtistEditorPage extends VerticalLayout implements HasUrlParameter<String> {

    // UI
    private final Image artistAvatar;
    private final Text artistName;

    private final CrudListBox<Track> trackList;
    private final CrudListBox<Album> albumList;

    // Services
    private final ArtistService artistService;
    private final TrackService trackService;
    private final AlbumService albumService;

    private Artist artist;

    @Autowired
    public ArtistEditorPage(ArtistService artistService, TrackService trackService, AlbumService albumService, MapperFactory mapperFactory) {
        this.artistService = artistService;
        this.trackService = trackService;
        this.albumService = albumService;

        artistName = new Text("");
        artistAvatar = new Image();

        albumList = createAlbumList(mapperFactory);
        trackList = createTrackList(mapperFactory);

        albumList.setHeight("300px");
        trackList.setHeight("300px");

        HorizontalLayout labelLayout = new HorizontalLayout(artistAvatar, artistName);
        labelLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        add(labelLayout);
        add(new HorizontalLayout(new VerticalLayout(new Text("Singles"), trackList), new VerticalLayout(new Text("Albums"), albumList))); //, panel("singiels")));
    }

    private void bindArtist(Artist artist) {
        artistName.setText(artist.getName());

        artistService.getThumbnailById(artist.getId()).ifPresentOrElse((imageResource) -> {
            artistAvatar.setSrc(new StreamResource(artist.getId(), imageResource::getInputStream));
        }, () -> {
            artistAvatar.setSrc("https://www.theatromarrakech.com/wp-content/plugins/urvenue-plugin/images/placeholder.artist.jpg");
        });

        artistAvatar.setAlt("artist avatar#" + artist.getId());
        artistAvatar.setWidth("96px");
        artistAvatar.setWidth("96px");

        albumList.refresh();
        trackList.refresh();
    }

    private CrudListBox<Album> createAlbumList(MapperFactory mapperFactory) {
        FormBuilder<AlbumFormDTO> albumFormBuilder = new FormBuilder<>(AlbumFormDTO.class);
        albumFormBuilder.setHeader("Create new album!");
        albumFormBuilder.addFields(new FormBuilder.FormField("name", "Name"));

        DtoFormDialog<Album, AlbumFormDTO> createFormDialog = new DtoFormDialog<>(
                Album.class,
                albumFormBuilder.build(),
                mapperFactory.createConverter());

        DtoFormDialog<Album, AlbumFormDTO> updateFormDialog = new DtoFormDialog<>(
                Album.class,
                albumFormBuilder.build(),
                mapperFactory.createConverter());

        CrudListBox<Album> albumList = new CrudListBox<>(createFormDialog, updateFormDialog, new CrudListBox.DataProvider<>() {
            @Override
            public Album create(Album entity, ImageResource imageResource) {
                entity.setArtist(artist);

                return imageResource != null ?
                        albumService.create(entity, imageResource) :
                        albumService.create(entity);
            }

            @Override
            public Album update(Album entity, ImageResource imageResource) {
                return imageResource != null ?
                        albumService.update(entity, imageResource) :
                        albumService.update(entity);
            }
            @Override
            public void delete(Album entity) {
                albumService.delete(entity);
            }

            @Override
            public Collection<Album> getAll() {
                return albumService.getByArtistId(artist.getId());
            }
        });

        albumList.setRenderer(new ComponentRenderer<>() {
            @Override
            public Component createComponent(Album album) {
                Details details = new Details();

                Image image = new Image();
                albumService.getThumbnailById(album.getId()).ifPresentOrElse(
                        (resource) -> image.setSrc(ComponentUtils.imageFromByteArray(resource)),
                        () -> image.setSrc("img/placeholder.jpg"));
                image.setWidth("128px");
                image.setHeight("128px");
                HorizontalLayout horizontalLayout = new HorizontalLayout(
                        image,
                        new Text(album.getName()));
                horizontalLayout.setAlignItems(Alignment.CENTER);

                details.setSummary(horizontalLayout);

                return details;
            }
        });

        return albumList;
    }

    private CrudListBox<Track> createTrackList(MapperFactory mapperFactory) {
        FormBuilder<TrackFormDTO> songFormBuilder = new FormBuilder<>(TrackFormDTO.class);
        songFormBuilder.setHeader("Add song to album!");
        songFormBuilder.addFields(new FormBuilder.FormField("name", "Name"));

        Form<TrackFormDTO> createForm = songFormBuilder.build();
        addTrackUpload(createForm);

        Form<TrackFormDTO> updateForm = songFormBuilder.build();
        addTrackUpload(updateForm);

        DtoFormDialog<Track, TrackFormDTO> createFormDialog = new DtoFormDialog<>(
                Track.class,
                createForm,
                mapperFactory.createConverter());

        DtoFormDialog<Track, TrackFormDTO> updateFormDialog = new DtoFormDialog<>(
                Track.class,
                updateForm,
                mapperFactory.createConverter());

        CrudListBox<Track> trackList = new CrudListBox<>(createFormDialog, updateFormDialog, new CrudListBox.DataProvider<>() {
            @Override
            public Track create(Track entity, ImageResource imageResource) {
                entity.setArtist(artist);

                return imageResource != null ?
                        trackService.create(entity, imageResource) :
                        trackService.create(entity);
            }

            @Override
            public Track update(Track entity, ImageResource imageResource) {
                return imageResource != null ?
                        trackService.update(entity, imageResource) :
                        trackService.update(entity);
            }

            @Override
            public void delete(Track entity) {
                trackService.delete(entity);
            }

            @Override
            public Collection<Track> getAll() {
                return trackService.getTracksByArtistId(artist.getId());
            }
        });

        trackList.setRenderer(new ComponentRenderer<>() {
            @Override
            public Component createComponent(Track track) {
                return new TrackItem(track, trackService.getThumbnailById(track.getId()));
            }
        });

        return trackList;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        Optional<Artist> artistOptional = artistService.findById(parameter);

        if (artistOptional.isPresent()) {
            this.artist = artistOptional.get();
            this.bindArtist(artist);
        } else {
            event.rerouteTo(Routes.NOT_FOUND);
        }
    }

    private void addTrackUpload(Form<TrackFormDTO> form) {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(AudioResource.ACCEPTED_TYPES);
        upload.setMaxFiles(1);
        upload.setMaxFileSize((int) AudioResource.MAX_FILE_SIZE);
        upload.addFinishedListener(event ->
            form.getDTO().setAudioResource(new AudioResource() {
                @Override
                public InputStream getInputStream() {
                    return buffer.getInputStream();
                }

                @Override
                public String getName() {
                    return buffer.getFileData().getFileName();
                }

                @Override
                public String getExtension() {
                    return FilenameUtils.getExtension(buffer.getFileData().getFileName());
                }

                @Override
                public String getMimeType() {
                    return buffer.getFileData().getMimeType();
                }
            })
        );

        form.getBody().addFormItem(upload, "Track File");
    }

}
