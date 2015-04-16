package com.psddev.ij.util;

public class StringUtil {
    public static String firstNonNull(String... strings) {
        if (strings != null) {
            for (int i = 0; i < strings.length; ++i) {
                if (strings[i] != null) {
                    return strings[i];
                }
            }
        }

        return null;
    }
}
