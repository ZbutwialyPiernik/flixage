package com.zbutwialypiernik.flixage.ui.admin.dashboard;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import com.zbutwialypiernik.flixage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class DashboardPanel extends VerticalLayout {

    private final UserService userService;

    @Autowired
    public DashboardPanel(UserService userService) {
        this.userService = userService;
    }

}
