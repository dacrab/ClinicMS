package com.clinicms.model;

import com.clinicms.util.CsvUtil;

/**
 * Αναπαρασταση ραντεβου – συνδεει ασθενη με ιατρο σε συγκεκριμενη ημερομηνια/ωρα.
 * Κυκλος ζωης: SCHEDULED → COMPLETED η CANCELLED.
 */
public class Appointment {

    public enum Status { SCHEDULED, COMPLETED, CANCELLED }

    private int id;
    private int patientId;
    private int doctorId;
    private String date;
    private String timeSlot;
    private String reason;
    private Status status;
    private String notes;

    public Appointment(int id, int patientId, int doctorId, String date,
                       String timeSlot, String reason, Status status, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.reason = reason;
        this.status = status;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Checks double-booking: same doctor, date, time, both non-cancelled
    public boolean conflictsWith(Appointment other) {
        return this.doctorId == other.doctorId
                && this.date.equals(other.date)
                && this.timeSlot.equals(other.timeSlot)
                && this.status != Status.CANCELLED
                && other.status != Status.CANCELLED;
    }

    public String toCsv() {
        return id + "," + patientId + "," + doctorId + "," + CsvUtil.escape(date) + "," + CsvUtil.escape(timeSlot)
                + "," + CsvUtil.escape(reason) + "," + status.name() + "," + CsvUtil.escape(notes);
    }

    public static Appointment fromCsv(String line) {
        String[] p = line.split(",", 8);
        return new Appointment(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()),
                Integer.parseInt(p[2].trim()), CsvUtil.unescape(p[3]), CsvUtil.unescape(p[4]),
                CsvUtil.unescape(p[5]), Status.valueOf(p[6].trim()), CsvUtil.unescape(p[7]));
    }

    @Override
    public String toString() { return "Appointment #" + id + " [" + date + " " + timeSlot + "] " + status; }
}
