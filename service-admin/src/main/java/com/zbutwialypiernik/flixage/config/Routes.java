package com.zbutwialypiernik.flixage.config;

public class Routes {

    private Routes() {}

    public static final String LOGIN = "login";

    public static final String LOGOUT = "logout";

    public static final String ADMIN = "admin";

    public static final String NOT_FOUND = "404";

    public static final String DASHBOARD = "dashboard";

    public static final String USERS = "users";

    public static final String LIBRARY = "library";

    public static final String ARTISTS = LIBRARY + "/artists";

    public static final String ALBUMS = LIBRARY + "/albums";

}
