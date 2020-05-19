package com.zbutwialypiernik.flixage.ui.error;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteNotFoundError;
import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.util.SecurityUtils;

import javax.servlet.http.HttpServletResponse;

public class RouteNotFoundHandler extends RouteNotFoundError {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<NotFoundException> parameter) {
        if (SecurityUtils.isUserLoggedIn()) {
            event.rerouteTo(Routes.NOT_FOUND);
        } else {
            event.rerouteTo(Routes.LOGIN);
        }

        return HttpServletResponse.SC_NOT_FOUND;
    }
}
