package com.zbutwialypiernik.flixage.ui.component.crud.impl;

import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.UIScope;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.ui.page.artist.ArtistEditorPage;
import com.zbutwialypiernik.flixage.ui.component.crud.PaginatedCrud;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import com.zbutwialypiernik.flixage.ui.component.form.Form;
import com.zbutwialypiernik.flixage.ui.component.form.FormBuilder;
import com.zbutwialypiernik.flixage.ui.component.form.dto.ArtistForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@UIScope
@Component
public class ArtistCrud extends PaginatedCrud<Artist, ArtistForm> {

    @Autowired
    public ArtistCrud(ArtistService artistService, MapperFactory factory) {
        super(Artist.class);

        setService(artistService);
        setForm(createForm("Artist"), factory.createMapper());

        addColumn(Artist::getName).setHeader("Name");
        addComponentColumn(artist -> new RouterLink("Editor", ArtistEditorPage.class, artist.getId())).setHeader("Artist Library");
    }

    public Form<ArtistForm> createForm(String header) {
        var generator = new FormBuilder<>(ArtistForm.class);
        generator.setHeader(header);

        return generator.build();
    }

}
