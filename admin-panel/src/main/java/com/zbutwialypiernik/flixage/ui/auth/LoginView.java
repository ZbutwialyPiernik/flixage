package com.zbutwialypiernik.flixage.ui.auth;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route
@PWA(name = "Cos tam xD", shortName = "COS")
@PageTitle("You must login to continue...")
public class LoginView extends VerticalLayout {

    public LoginView() {
        LoginOverlay login = new LoginOverlay();
        login.setAction("login");
        login.setOpened(true);
        login.setTitle("Flixage");
        login.setForgotPasswordButtonVisible(false);
        login.setDescription("");

        getElement().appendChild(login.getElement());
    }
}
