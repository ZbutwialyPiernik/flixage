package com.zbutwialypiernik.flixage.config;

public enum Resources {

    ARTISTS_AVATARS("img/artists/"),
    AUDIO_FILES("audio/");

    private final String path;

    Resources(String path) {
        this.path = path;
    }

    public String compile(String fileName) {
        return path + fileName + "." + IMAGE_EXTENSION;
    }

    public static final String IMAGE_EXTENSION = "png";

}
