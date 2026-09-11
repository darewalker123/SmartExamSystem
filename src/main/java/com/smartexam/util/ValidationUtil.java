package com.smartexam.util;

import java.util.Set;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Set<String> ROLES = Set.of("admin", "teacher", "student");
    private static final Set<String> ANSWERS = Set.of("A", "B", "C", "D");

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isEmail(String value) {
        return value != null && EMAIL_PATTERN.matcher(value).matches();
    }

    public static boolean isRole(String value) {
        return value != null && ROLES.contains(value.toLowerCase());
    }

    public static boolean isAnswer(String value) {
        return value != null && ANSWERS.contains(value.toUpperCase());
    }
}
