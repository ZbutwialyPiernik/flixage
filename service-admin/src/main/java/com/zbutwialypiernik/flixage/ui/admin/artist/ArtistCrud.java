package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.ui.admin.artist.form.ArtistFormDTO;
import com.zbutwialypiernik.flixage.ui.component.crud.Crud;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.FormBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@UIScope
public class ArtistCrud extends Crud<Artist> {

    @Autowired
    public ArtistCrud(ArtistService artistService, MapperFactory factory) {
        super(Artist.class);
        setUpdateForm(createForm("Create artist"), factory.createConverter());
        setCreationForm(createForm("Update artist"), factory.createConverter());

        setService(artistService);

        addColumn(Artist::getName).setHeader("Name");
        addComponentColumn(artist ->
                new RouterLink("Editor", ArtistEditorPage.class, artist.getId())
        ).setHeader("Artist Library");
    }

    public Form<ArtistFormDTO> createForm(String header) {
        FormBuilder<ArtistFormDTO> generator = new FormBuilder<>(ArtistFormDTO.class);
        generator.setHeader(header);
        generator.addFields(new FormBuilder.FormField("name", "Name"));
        Form<ArtistFormDTO> form = generator.build();

        return form;
    }

}
