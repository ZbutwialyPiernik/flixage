package com.zbutwialypiernik.flixage.ui.admin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.ui.admin.dashboard.DashboardPanel;
import com.zbutwialypiernik.flixage.ui.admin.artist.ArtistCrud;
import com.zbutwialypiernik.flixage.ui.admin.user.UserCrud;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

@Route(Routes.ADMIN)
public class AdminPanelView extends AppLayout {

    /**
     * Linking tab label with corresponding admin panel.
     */
    private final HashMap<String, Component> labelToPanelMap = new HashMap<>();

    @Autowired
    public AdminPanelView(DashboardPanel dashboardPanel, UserCrud userCrudPanel, ArtistCrud artistCrudPanel) {
        labelToPanelMap.put("Dashboard", dashboardPanel);
        labelToPanelMap.put("Users", userCrudPanel);
        labelToPanelMap.put("Library", artistCrudPanel);

        Tabs tabs = new Tabs(
                createTab(VaadinIcon.DASHBOARD, "Dashboard"),
                createTab(VaadinIcon.USERS, "Users"),
                createTab(VaadinIcon.MUSIC, "Library"));

        tabs.addSelectedChangeListener(event -> setContentByTab(event.getSelectedTab()));

        FlexLayout tabsCenter = new FlexLayout();
        tabsCenter.setWidthFull();
        tabsCenter.add(tabs);
        tabsCenter.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Image logo = new Image("img/logo.png", "logo");
        logo.setHeight("48px");

        Anchor logoutButton = new Anchor(Routes.LOGOUT, createTab(VaadinIcon.EXIT, "Logout"));

        addToNavbar(false, logo, tabsCenter, logoutButton);
        setContent(dashboardPanel);
    }

    private void setContentByTab(Tab tab) {
        Component selectedContent = labelToPanelMap.get(tab.getLabel());
        setContent(selectedContent);
    }

    private Tab createTab(VaadinIcon icon, String label) {
        return new Tab(new Icon(icon), new Text(label));
    }


}
