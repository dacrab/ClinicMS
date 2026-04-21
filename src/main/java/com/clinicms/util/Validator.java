package com.clinicms.util;

import java.util.regex.Pattern;

/**
 * Utility class providing static validation helpers used by the controllers.
 */
public final class Validator {

    // ── Regex patterns ─────────────────────────────────────────────────────────

    private static final Pattern DATE_PATTERN =
            Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");

    private static final Pattern TIME_PATTERN =
            Pattern.compile("^\\d{2}:\\d{2}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[+0-9\\-\\s]{7,15}$");

    // ── Public validators ──────────────────────────────────────────────────────

    /** Returns true if the string is non-null and non-blank. */
    public static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    /** Returns true if the value matches dd/MM/yyyy. */
    public static boolean isValidDate(String date) {
        return date != null && DATE_PATTERN.matcher(date).matches();
    }

    /** Returns true if the value matches HH:mm. */
    public static boolean isValidTime(String time) {
        return time != null && TIME_PATTERN.matcher(time).matches();
    }

    /** Returns true if the value looks like a valid e-mail address. */
    public static boolean isValidEmail(String email) {
        return email == null || email.isBlank() || EMAIL_PATTERN.matcher(email).matches();
    }

    /** Returns true if the value looks like a valid phone number. */
    public static boolean isValidPhone(String phone) {
        return phone == null || phone.isBlank() || PHONE_PATTERN.matcher(phone).matches();
    }

    private Validator() {} // utility class – no instantiation
}
