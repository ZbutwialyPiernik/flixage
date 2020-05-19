package com.zbutwialypiernik.flixage.ui.auth;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.zbutwialypiernik.flixage.config.Routes;

@Route(Routes.LOGIN)
@Theme(value = Lumo.class)
@PageTitle("You must login to continue...")
public class LoginView extends VerticalLayout {

    public LoginView() {
        LoginOverlay login = new LoginOverlay();
        login.setAction("login");
        login.setOpened(true);
        login.setTitle("Flixage");
        login.setForgotPasswordButtonVisible(false);
        login.setDescription("");

        add(login);
    }
}
