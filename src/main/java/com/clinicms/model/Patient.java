package com.clinicms.model;

/**
 * Represents a Patient registered in the Clinic Management System.
 *
 * <p>Stores personal and medical information including a free-text
 * {@code medicalHistory} field for allergies, chronic conditions, and notes.
 *
 * <p><strong>OOP concepts demonstrated:</strong>
 * <ul>
 *   <li>Encapsulation – all fields are private, exposed via getters/setters</li>
 *   <li>CSV serialisation – {@link #toCsv()} / {@link #fromCsv(String)}</li>
 * </ul>
 *
 * <p><strong>CSV format:</strong>
 * {@code id,name,dateOfBirth,gender,phone,email,medicalHistory}
 */
public class Patient {

    private int id;
    private String name;
    private String dateOfBirth; // stored as dd/MM/yyyy string
    private String gender;
    private String phone;
    private String email;
    private String medicalHistory;

    /** Full constructor. */
    public Patient(int id, String name, String dateOfBirth, String gender,
                   String phone, String email, String medicalHistory) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.medicalHistory = medicalHistory;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    /**
     * Serialises this Patient to a CSV line.
     * Format: id,name,dateOfBirth,gender,phone,email,medicalHistory
     */
    public String toCsv() {
        return id + "," + escape(name) + "," + escape(dateOfBirth) + "," + escape(gender)
                + "," + escape(phone) + "," + escape(email) + "," + escape(medicalHistory);
    }

    /** Deserialises a Patient from a CSV line produced by {@link #toCsv()}. */
    public static Patient fromCsv(String line) {
        String[] parts = line.split(",", 7);
        return new Patient(
                Integer.parseInt(parts[0].trim()),
                unescape(parts[1]),
                unescape(parts[2]),
                unescape(parts[3]),
                unescape(parts[4]),
                unescape(parts[5]),
                unescape(parts[6])
        );
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace(",", "&#44;");
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("&#44;", ",").trim();
    }

    @Override
    public String toString() {
        return name + " (DOB: " + dateOfBirth + ")";
    }
}
