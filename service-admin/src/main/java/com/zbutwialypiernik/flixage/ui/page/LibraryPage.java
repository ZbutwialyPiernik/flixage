package com.zbutwialypiernik.flixage.ui.page;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.ui.component.crud.impl.ArtistCrud;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;

@Route(value = Routes.LIBRARY, layout = RootPage.class)
public class LibraryPage extends VerticalLayout {

    public LibraryPage(ArtistService artistService, MapperFactory factory) {
        add(new ArtistCrud(artistService, factory));
    }
}
