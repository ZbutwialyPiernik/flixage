package com.zbutwialypiernik.flixage.service;

import java.util.Random;

/**
 * Service for generating unique numbers, to share playlists easier.
 */
public class ShareCodeGenerator {

    public static final int ALPHABET_LENGTH = 26;

    public static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWYXZ".toCharArray();

    public static final Random RANDOM = new Random();

    public static String generateCode(int length) {
        final var builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET[RANDOM.nextInt(ALPHABET_LENGTH)]);
        }

        return builder.toString();
    }


}
