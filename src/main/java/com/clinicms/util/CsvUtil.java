package com.clinicms.util;

/** Escape/unescape κομματων για ασφαλη αποθηκευση σε CSV. */
public final class CsvUtil {

    private CsvUtil() {}

    public static String escape(String s) {
        return s == null ? "" : s.replace(",", "&#44;");
    }

    public static String unescape(String s) {
        return s == null ? "" : s.replace("&#44;", ",").trim();
    }
}
