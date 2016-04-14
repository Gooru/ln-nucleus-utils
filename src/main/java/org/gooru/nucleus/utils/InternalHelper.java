package org.gooru.nucleus.utils;

import java.util.regex.Pattern;

public final class InternalHelper {

    private InternalHelper() {
        throw new AssertionError();
    }

    private static final Pattern PATTERN = Pattern.compile("[^a-zA-Z0-9]+");

    public static String replaceSpecialCharWithUnderscore(String name) {
        return PATTERN.matcher(name).replaceAll("_");
    }
}
