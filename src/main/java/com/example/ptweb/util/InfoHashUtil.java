package com.example.ptweb.util;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class InfoHashUtil {
    public static @NotNull String parseInfoHash(String encoded) throws IllegalArgumentException {
        try {
            StringBuilder r = new StringBuilder();
            for (int i = 0; i < encoded.length(); i++) {
                char c = encoded.charAt(i);
                if (c == '%') {
                    r.append(encoded.charAt(i + 1));
                    r.append(encoded.charAt(i + 2));
                    i = i + 2;
                } else {
                    r.append(String.format("%02x", (int) c));
                }
            }
            return r.toString().toLowerCase(Locale.ROOT);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode info_hash: " + encoded);
        }
    }
}
