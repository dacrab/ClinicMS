package com.clinicms.model;

/**
 * Represents a general-practice Doctor in the Clinic Management System.
 *
 * <p>A Doctor is the base entity in the medical staff hierarchy.
 * {@link Specialist} extends this class for doctors with a sub-specialty.
 *
 * <p><strong>OOP concepts demonstrated:</strong>
 * <ul>
 *   <li>Encapsulation – all fields are private with public getters/setters</li>
 *   <li>CSV serialisation/deserialisation via {@link #toCsv()} / {@link #fromCsv(String)}</li>
 *   <li>Inheritance base – extended by {@link Specialist}</li>
 * </ul>
 *
 * <p><strong>CSV format:</strong> {@code id,name,specialty,phone,email}
 */
public class Doctor {

    private int id;
    private String name;
    private String specialty;
    private String phone;
    private String email;

    /** Full constructor. */
    public Doctor(int id, String name, String specialty, String phone, String email) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * Serialises this Doctor to a CSV line.
     * Format: id,name,specialty,phone,email
     */
    public String toCsv() {
        return id + "," + escape(name) + "," + escape(specialty) + "," + escape(phone) + "," + escape(email);
    }

    /**
     * Deserialises a Doctor from a CSV line produced by {@link #toCsv()}.
     */
    public static Doctor fromCsv(String line) {
        String[] parts = line.split(",", 5);
        return new Doctor(
                Integer.parseInt(parts[0].trim()),
                unescape(parts[1]),
                unescape(parts[2]),
                unescape(parts[3]),
                unescape(parts[4])
        );
    }

    /** Escapes commas inside a field value. */
    private static String escape(String s) {
        return s == null ? "" : s.replace(",", "&#44;");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("&#44;", ",").trim();
    }

    @Override
    public String toString() {
        return "Dr. " + name + " (" + specialty + ")";
    }
}
