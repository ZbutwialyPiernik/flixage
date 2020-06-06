package com.zbutwialypiernik.flixage.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExtensionUtils {

    private static final LinkedHashMap<String, String> types = new LinkedHashMap<>() {{
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("mp3", "audio/mp3");
        put("wav", "audio/wav");
    }};

    /**
     * @param extension the extension of file without dot
     * @return corresponding mime type to extension, null if extension is unknown
     */
    public static String getMimeType(String extension) {
        return types.get(extension.toLowerCase());
    }

    /**
     * @param mimeType the mime type of file
     * @return corresponding extension to mime type, null if mime type is unknown
     */
    public static String getExtension(String mimeType) {
        return types.entrySet().stream()
                .filter((entry) -> entry.getValue().equals(mimeType.toLowerCase()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

}
