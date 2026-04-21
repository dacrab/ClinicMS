package com.clinicms.model;

/**
 * Represents a Specialist Doctor — a {@link Doctor} with an additional
 * sub-specialty and maximum weekly patient capacity.
 *
 * <p>This class demonstrates OOP <strong>inheritance</strong>: a Specialist
 * IS-A Doctor with extra domain-specific attributes.
 *
 * <p>CSV format (extends Doctor's CSV with 2 extra fields):
 * <pre>id,name,specialty,phone,email,subSpecialty,maxWeeklyPatients</pre>
 */
public class Specialist extends Doctor {

    /** More focused area within the main specialty (e.g. "Interventional Cardiology"). */
    private String subSpecialty;

    /** Maximum number of patients this specialist accepts per week. */
    private int maxWeeklyPatients;

    /**
     * Constructs a Specialist with all fields.
     *
     * @param id                unique identifier
     * @param name              full name
     * @param specialty         main specialty (e.g. "Cardiology")
     * @param phone             contact phone
     * @param email             contact email
     * @param subSpecialty      focused sub-area
     * @param maxWeeklyPatients weekly capacity
     */
    public Specialist(int id, String name, String specialty, String phone, String email,
                      String subSpecialty, int maxWeeklyPatients) {
        super(id, name, specialty, phone, email);
        this.subSpecialty      = subSpecialty;
        this.maxWeeklyPatients = maxWeeklyPatients;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    /** @return the sub-specialty of this specialist */
    public String getSubSpecialty() { return subSpecialty; }

    /** @param subSpecialty the new sub-specialty */
    public void setSubSpecialty(String subSpecialty) { this.subSpecialty = subSpecialty; }

    /** @return the maximum number of patients per week */
    public int getMaxWeeklyPatients() { return maxWeeklyPatients; }

    /** @param maxWeeklyPatients the new weekly capacity */
    public void setMaxWeeklyPatients(int maxWeeklyPatients) { this.maxWeeklyPatients = maxWeeklyPatients; }

    /**
     * Serialises this Specialist to a CSV line.
     * The prefix {@code S:} distinguishes it from a plain Doctor line.
     *
     * @return CSV representation
     */
    @Override
    public String toCsv() {
        return "S:" + super.toCsv() + "," + escape(subSpecialty) + "," + maxWeeklyPatients;
    }

    /**
     * Deserialises a Specialist from a CSV line produced by {@link #toCsv()}.
     *
     * @param line CSV line (with leading {@code S:} stripped already)
     * @return a new {@link Specialist} instance
     */
    public static Specialist fromCsv(String line) {
        // line format: id,name,specialty,phone,email,subSpecialty,maxWeeklyPatients
        String[] parts = line.split(",", 7);
        return new Specialist(
                Integer.parseInt(parts[0].trim()),
                unescape(parts[1]),
                unescape(parts[2]),
                unescape(parts[3]),
                unescape(parts[4]),
                unescape(parts[5]),
                Integer.parseInt(parts[6].trim())
        );
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace(",", "&#44;");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("&#44;", ",").trim();
    }

    /**
     * Returns a human-readable description of this specialist.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Dr. " + getName() + " (" + getSpecialty() + " › " + subSpecialty + ") [Specialist]";
    }
}
