package com.clinicms.model;

import com.clinicms.util.CsvUtil;

/**
 * Εξειδικευμενος ιατρος – επεκτεινει τον {@link Doctor}
 * με υπο-ειδικοτητα και μεγιστο εβδομαδιαιο αριθμο ασθενων.
 */
public class Specialist extends Doctor {

    private String subSpecialty;
    private int maxWeeklyPatients;

    public Specialist(int id, String name, String specialty, String phone, String email,
                      String subSpecialty, int maxWeeklyPatients) {
        super(id, name, specialty, phone, email);
        this.subSpecialty = subSpecialty;
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
        String[] p = line.split(",", 7);
        return new Specialist(Integer.parseInt(p[0].trim()),
                CsvUtil.unescape(p[1]), CsvUtil.unescape(p[2]),
                CsvUtil.unescape(p[3]), CsvUtil.unescape(p[4]),
                CsvUtil.unescape(p[5]), Integer.parseInt(p[6].trim()));
    }

    @Override
    public String toString() {
        return "Dr. " + getName() + " (" + getSpecialty() + " > " + subSpecialty + ") [Specialist]";
    }
}
