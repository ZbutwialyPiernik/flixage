package com.zbutwialypiernik.flixage.ui.page;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = Routes.DASHBOARD, layout = RootPage.class)
public class DashboardPage extends VerticalLayout {

    private final UserService userService;

    @Autowired
    public DashboardPage(UserService userService) {
        this.userService = userService;
    }

}
