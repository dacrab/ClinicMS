package com.clinicms.model;

import com.clinicms.util.CsvUtil;

/**
 * Represents a Specialist Doctor — a {@link Doctor} with an additional
 * sub-specialty and maximum weekly patient capacity.
 *
 * <p>CSV format (extends Doctor's CSV with 2 extra fields):
 * <pre>id,name,specialty,phone,email,subSpecialty,maxWeeklyPatients</pre>
 */
public class Specialist extends Doctor {

    private String subSpecialty;
    private int maxWeeklyPatients;

    public Specialist(int id, String name, String specialty, String phone, String email,
                      String subSpecialty, int maxWeeklyPatients) {
        super(id, name, specialty, phone, email);
        this.subSpecialty      = subSpecialty;
        this.maxWeeklyPatients = maxWeeklyPatients;
    }

    public String getSubSpecialty() { return subSpecialty; }
    public void setSubSpecialty(String subSpecialty) { this.subSpecialty = subSpecialty; }

    public int getMaxWeeklyPatients() { return maxWeeklyPatients; }
    public void setMaxWeeklyPatients(int maxWeeklyPatients) { this.maxWeeklyPatients = maxWeeklyPatients; }

    @Override
    public String toCsv() {
        return "S:" + super.toCsv() + "," + CsvUtil.escape(subSpecialty) + "," + maxWeeklyPatients;
    }

    public static Specialist fromCsv(String line) {
        String[] parts = line.split(",", 7);
        return new Specialist(
                Integer.parseInt(parts[0].trim()),
                CsvUtil.unescape(parts[1]),
                CsvUtil.unescape(parts[2]),
                CsvUtil.unescape(parts[3]),
                CsvUtil.unescape(parts[4]),
                CsvUtil.unescape(parts[5]),
                Integer.parseInt(parts[6].trim())
        );
    }

    @Override
    public String toString() {
        return "Dr. " + getName() + " (" + getSpecialty() + " › " + subSpecialty + ") [Specialist]";
    }
}
