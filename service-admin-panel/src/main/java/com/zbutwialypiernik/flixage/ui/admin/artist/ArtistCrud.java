package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.UIScope;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.ui.admin.artist.form.ArtistFormDTO;
import com.zbutwialypiernik.flixage.ui.component.ComponentUtils;
import com.zbutwialypiernik.flixage.ui.component.crud.Crud;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.FormBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@UIScope
public class ArtistCrud extends Crud<Artist> {

    private final ArtistEditorDialog artistEditorDialog;

    @Autowired
    public ArtistCrud(ArtistService artistService, MapperFactory factory) {
        super(Artist.class);
        this.artistEditorDialog = new ArtistEditorDialog(artistService, factory);

        setUpdateForm(createForm("Create artist"), factory.createConverter());
        setCreationForm(createForm("Update artist"), factory.createConverter());

        setDataProvider(artistService);

        addComponentColumn(artist -> {
            Image image = ComponentUtils.imageFromByteArray(artist.getArtistAvatar(), artist.getId(), "artist avatar#" + artist.getId());
            image.setHeight("64px");
            image.setWidth("64px");
            return image;
        }).setHeader("Avatar");
        addColumn(Artist::getName)
                .setHeader("Name");
        addComponentColumn(artist -> new Button("Library",
                    (event) ->  {
                        artistEditorDialog.setArtist(artistService.findByIdEagerly(artist.getId()).get());
                        artistEditorDialog.open();
                    })).setHeader("Artist Library");
    }

    public Form<ArtistFormDTO> createForm(String header) {
        FormBuilder<ArtistFormDTO> generator = new FormBuilder<>(ArtistFormDTO.class);
        generator.setHeader(header);
        generator.addFields(new FormBuilder.FormField("name", "Name"));
        Form<ArtistFormDTO> form = generator.build();

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(ImageService.ACCEPTED_TYPES);
        upload.setMaxFiles(1);
        // 3 MB
        upload.setMaxFileSize(3000000);
        upload.addFinishedListener(event -> {
            try {
                form.getEntity().setArtistAvatar(buffer.getInputStream().readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        form.getBody().add(upload);
        return form;
    }
}
