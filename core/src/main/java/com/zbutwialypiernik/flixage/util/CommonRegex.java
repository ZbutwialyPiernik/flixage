package com.zbutwialypiernik.flixage.util;

public class CommonRegex {

    private CommonRegex() {}

    public static final String UUID = "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}";

    public static final String JWT_TOKEN = "[a-zA-Z0-9\\-_]+?\\.[a-zA-Z0-9\\-_]+?\\.([a-zA-Z0-9\\-_]+)";

}
