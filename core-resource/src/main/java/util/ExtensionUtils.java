package util;

import java.util.Map;

public class ExtensionUtils {

    private static final Map<String, String> types = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "mp3", "audio/mp3",
            "wav", "audio/wav");


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
