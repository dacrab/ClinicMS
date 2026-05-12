package com.clinicms.util;

import java.util.regex.Pattern;

public final class Validator {

    private static final Pattern DATE_PAT = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    private static final Pattern EMAIL_PAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_PAT = Pattern.compile("^[+0-9\\-\\s]{7,15}$");

    public static boolean notBlank(String s) { return s != null && !s.isBlank(); }
    public static boolean isValidDate(String d) { return d != null && DATE_PAT.matcher(d).matches(); }
    public static boolean isValidEmail(String e) { return e == null || e.isBlank() || EMAIL_PAT.matcher(e).matches(); }
    public static boolean isValidPhone(String p) { return p == null || p.isBlank() || PHONE_PAT.matcher(p).matches(); }

    private Validator() {}
}
