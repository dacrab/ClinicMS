package com.clinicms.model;

import com.clinicms.util.CsvUtil;

/**
 * Represents an Appointment in the Clinic Management System.
 *
 * <p>An Appointment links a {@link Patient} to a {@link Doctor} at a specific
 * date and 30-minute time slot.  Each appointment has a lifecycle status:
 * {@code SCHEDULED → COMPLETED} or {@code SCHEDULED → CANCELLED}.
 *
 * <p><strong>CSV format:</strong>
 * {@code id,patientId,doctorId,date,timeSlot,reason,status,notes}
 */
public class Appointment {

    public enum Status {
        SCHEDULED, COMPLETED, CANCELLED
    }

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

    /**
     * Checks whether this appointment conflicts (same doctor, same date + time) with another.
     */
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
        String[] parts = line.split(",", 8);
        return new Appointment(
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim()),
                CsvUtil.unescape(parts[3]),
                CsvUtil.unescape(parts[4]),
                CsvUtil.unescape(parts[5]),
                Status.valueOf(parts[6].trim()),
                CsvUtil.unescape(parts[7])
        );
    }

    @Override
    public String toString() {
        return "Appointment #" + id + " [" + date + " " + timeSlot + "] – " + status;
    }
}
