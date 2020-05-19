package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
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

import java.util.Optional;


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

        setService(artistService);

        addComponentColumn(artist -> {
            Optional<byte[]> optional = artistService.getThumbnailById(artist.getId());

            Image image = optional.map(bytes -> ComponentUtils.imageFromByteArray(bytes, artist.getId() + ".png", "artist avatar#" + artist.getId()))
                    .orElseGet(() -> new Image("https://www.theatromarrakech.com/wp-content/plugins/urvenue-plugin/images/placeholder.artist.jpg", "placeholder"));

            image.setHeight("64px");
            image.setWidth("64px");

            return image;
        }).setHeader("Avatar");
        addColumn(Artist::getName).setHeader("Name");
        addComponentColumn(artist -> new Button("Library",
                (event) -> {
                    artistEditorDialog.setArtist(artist);
                    artistEditorDialog.open();
                })).setHeader("Artist Library");
    }

    public Form<ArtistFormDTO> createForm(String header) {
        FormBuilder<ArtistFormDTO> generator = new FormBuilder<>(ArtistFormDTO.class);
        generator.setHeader(header);
        generator.addFields(new FormBuilder.FormField("name", "Name"));
        Form<ArtistFormDTO> form = generator.build();



        return form;
    }
}
