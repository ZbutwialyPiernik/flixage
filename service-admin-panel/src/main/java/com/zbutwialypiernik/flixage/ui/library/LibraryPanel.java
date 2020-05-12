package com.zbutwialypiernik.flixage.ui.library;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class LibraryPanel extends VerticalLayout {

    private final PlaylistService service;

    @Autowired
    public LibraryPanel(PlaylistService service) {
        this.service = service;
    }

}

