package com.zbutwialypiernik.flixage.ui.page;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.zbutwialypiernik.flixage.config.Routes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Route(Routes.ADMIN)
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class RootPage extends AppLayout implements AfterNavigationObserver {

    private final HashMap<String, Class<? extends Component>> routes = new HashMap<>();

    private final HashMap<String, Tab> routeToTab = new LinkedHashMap<>();

    private final Tabs tabs;

    @Autowired
    public RootPage() {
        tabs = new Tabs();

        routes.put(Routes.DASHBOARD.toLowerCase(), DashboardPage.class);
        routes.put(Routes.USERS.toLowerCase(), UsersPage.class);
        routes.put(Routes.LIBRARY.toLowerCase(), LibraryPage.class);

        routeToTab.put(Routes.DASHBOARD.toLowerCase(), createTab(VaadinIcon.DASHBOARD, "Dashboard"));
        routeToTab.put(Routes.USERS.toLowerCase(), createTab(VaadinIcon.USERS, "Users"));
        routeToTab.put(Routes.LIBRARY.toLowerCase(), createTab(VaadinIcon.MUSIC, "Library"));

        routeToTab.values().forEach(tabs::add);

        tabs.addSelectedChangeListener(event -> {
            var routeClass = routes.get(event.getSelectedTab().getLabel().toLowerCase());
            UI.getCurrent().navigate(routeClass);
        });

        FlexLayout tabsCenter = new FlexLayout();
        tabsCenter.setWidthFull();
        tabsCenter.add(tabs);
        tabsCenter.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Image logo = new Image("img/logo.png", "logo");
        logo.setHeight("48px");

        Anchor logoutButton = new Anchor(Routes.LOGOUT, createTab(VaadinIcon.EXIT, "Logout"));

        addToNavbar(false, logo, tabsCenter, logoutButton);
    }

    private Tab createTab(VaadinIcon icon, String label) {
        return new Tab(new Icon(icon), new Text(label));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        System.out.println(event.getLocation().getFirstSegment());

        tabs.setSelectedTab(routeToTab.get(event.getLocation().getFirstSegment().toLowerCase()));
    }

}
