package com.zbutwialypiernik.flixage.ui.admin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import com.zbutwialypiernik.flixage.config.Routes;

@Route(Routes.NOT_FOUND)
public class EntityNotFoundPage extends VerticalLayout {

    public EntityNotFoundPage() {
        setAlignItems(Alignment.CENTER);
        add("404");
    }

}
