package com.clinicms.model;

import com.clinicms.util.CsvUtil;

/**
 * Represents a general-practice Doctor in the Clinic Management System.
 *
 * <p>A Doctor is the base entity in the medical staff hierarchy.
 * {@link Specialist} extends this class for doctors with a sub-specialty.
 *
 * <p><strong>CSV format:</strong> {@code id,name,specialty,phone,email}
 */
public class Doctor {

    private int id;
    private String name;
    private String specialty;
    private String phone;
    private String email;

    public Doctor(int id, String name, String specialty, String phone, String email) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
    }

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

    public String toCsv() {
        return id + "," + CsvUtil.escape(name) + "," + CsvUtil.escape(specialty)
                + "," + CsvUtil.escape(phone) + "," + CsvUtil.escape(email);
    }

    public static Doctor fromCsv(String line) {
        String[] parts = line.split(",", 5);
        return new Doctor(
                Integer.parseInt(parts[0].trim()),
                CsvUtil.unescape(parts[1]),
                CsvUtil.unescape(parts[2]),
                CsvUtil.unescape(parts[3]),
                CsvUtil.unescape(parts[4])
        );
    }

    @Override
    public String toString() {
        return "Dr. " + name + " (" + specialty + ")";
    }
}
