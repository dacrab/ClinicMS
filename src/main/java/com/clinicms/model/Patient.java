package com.clinicms.model;

import com.clinicms.util.CsvUtil;

public class Patient {

    private int id;
    private String name;
    private String dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String medicalHistory;

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

    public String toCsv() {
        return id + "," + CsvUtil.escape(name) + "," + CsvUtil.escape(dateOfBirth) + "," + CsvUtil.escape(gender)
                + "," + CsvUtil.escape(phone) + "," + CsvUtil.escape(email) + "," + CsvUtil.escape(medicalHistory);
    }

    public static Patient fromCsv(String line) {
        String[] p = line.split(",", 7);
        return new Patient(Integer.parseInt(p[0].trim()),
                CsvUtil.unescape(p[1]), CsvUtil.unescape(p[2]), CsvUtil.unescape(p[3]),
                CsvUtil.unescape(p[4]), CsvUtil.unescape(p[5]), CsvUtil.unescape(p[6]));
    }

    @Override
    public String toString() { return name + " (DOB: " + dateOfBirth + ")"; }
}
