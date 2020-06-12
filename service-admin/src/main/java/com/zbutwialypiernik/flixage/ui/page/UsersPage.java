package com.zbutwialypiernik.flixage.ui.page;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.service.UserService;
import com.zbutwialypiernik.flixage.ui.component.crud.impl.UserCrud;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.MapperFactory;
import com.zbutwialypiernik.flixage.ui.component.crud.mapper.OrikaMapperFactory;

@Route(value = Routes.USERS, layout = RootPage.class)
public class UsersPage extends VerticalLayout {

    public UsersPage(UserService service, MapperFactory factory) {
        add(new UserCrud(service, factory));
    }

}
