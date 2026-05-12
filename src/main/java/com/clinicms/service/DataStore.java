package com.clinicms.service;

import com.clinicms.model.Appointment;
import com.clinicms.model.Doctor;
import com.clinicms.model.Patient;
import com.clinicms.util.IdGenerator;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Singleton data store – in-memory collections persisted to CSV on every write.
public class DataStore {

    private static DataStore instance;

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    private static final String DATA_DIR = "data";
    private static final String DOCTORS_FILE = DATA_DIR + "/doctors.csv";
    private static final String PATIENTS_FILE = DATA_DIR + "/patients.csv";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "/appointments.csv";

    private final List<Doctor> doctors = new ArrayList<>();
    private final List<Patient> patients = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();

    private DataStore() {
        new File(DATA_DIR).mkdirs();
        loadAll();
    }

    // ── Doctors ────────────────────────────────────────────────────────────────

    public List<Doctor> getDoctors() { return List.copyOf(doctors); }

    public void addDoctor(Doctor d) { doctors.add(d); saveDoctors(); }

    public void updateDoctor(Doctor updated) {
        for (int i = 0; i < doctors.size(); i++)
            if (doctors.get(i).getId() == updated.getId()) { doctors.set(i, updated); break; }
        saveDoctors();
    }

    public void deleteDoctor(int id) {
        if (appointments.stream().anyMatch(a -> a.getDoctorId() == id && a.getStatus() == Appointment.Status.SCHEDULED))
            throw new IllegalStateException("Δεν μπορει να διαγραφει ιατρος με ενεργα ραντεβου.");
        doctors.removeIf(d -> d.getId() == id);
        saveDoctors();
    }

    public Optional<Doctor> findDoctorById(int id) {
        return doctors.stream().filter(d -> d.getId() == id).findFirst();
    }

    public List<Doctor> searchDoctors(String q) {
        String lower = q.toLowerCase();
        return doctors.stream()
                .filter(d -> d.getName().toLowerCase().contains(lower) || d.getSpecialty().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ── Patients ───────────────────────────────────────────────────────────────

    public List<Patient> getPatients() { return List.copyOf(patients); }

    public void addPatient(Patient p) { patients.add(p); savePatients(); }

    public void updatePatient(Patient updated) {
        for (int i = 0; i < patients.size(); i++)
            if (patients.get(i).getId() == updated.getId()) { patients.set(i, updated); break; }
        savePatients();
    }

    public void deletePatient(int id) {
        if (appointments.stream().anyMatch(a -> a.getPatientId() == id && a.getStatus() == Appointment.Status.SCHEDULED))
            throw new IllegalStateException("Δεν μπορει να διαγραφει ασθενης με ενεργα ραντεβου.");
        patients.removeIf(p -> p.getId() == id);
        savePatients();
    }

    public Optional<Patient> findPatientById(int id) {
        return patients.stream().filter(p -> p.getId() == id).findFirst();
    }

    public List<Patient> searchPatients(String q) {
        String lower = q.toLowerCase();
        return patients.stream()
                .filter(p -> p.getName().toLowerCase().contains(lower) || p.getPhone().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ── Appointments ───────────────────────────────────────────────────────────

    public List<Appointment> getAppointments() { return List.copyOf(appointments); }

    public void addAppointment(Appointment a) {
        checkDoubleBooking(a, -1);
        appointments.add(a);
        saveAppointments();
    }

    public void updateAppointment(Appointment updated) {
        checkDoubleBooking(updated, updated.getId());
        for (int i = 0; i < appointments.size(); i++)
            if (appointments.get(i).getId() == updated.getId()) { appointments.set(i, updated); break; }
        saveAppointments();
    }

    public void cancelAppointment(int id) {
        appointments.stream().filter(a -> a.getId() == id).findFirst()
                .ifPresent(a -> a.setStatus(Appointment.Status.CANCELLED));
        saveAppointments();
    }

    public void completeAppointment(int id) {
        appointments.stream().filter(a -> a.getId() == id).findFirst()
                .ifPresent(a -> a.setStatus(Appointment.Status.COMPLETED));
        saveAppointments();
    }

    private void checkDoubleBooking(Appointment incoming, int excludeId) {
        if (incoming.getStatus() == Appointment.Status.CANCELLED) return;
        boolean conflict = appointments.stream()
                .filter(a -> a.getId() != excludeId)
                .anyMatch(a -> a.conflictsWith(incoming));
        if (conflict)
            throw new IllegalStateException("Ο ιατρος εχει ηδη ραντεβου στις " + incoming.getDate() + " " + incoming.getTimeSlot());
    }

    // ── Persistence ────────────────────────────────────────────────────────────

    private void saveDoctors() { writeLines(DOCTORS_FILE, doctors.stream().map(Doctor::toCsv).collect(Collectors.toList())); }
    private void savePatients() { writeLines(PATIENTS_FILE, patients.stream().map(Patient::toCsv).collect(Collectors.toList())); }
    private void saveAppointments() { writeLines(APPOINTMENTS_FILE, appointments.stream().map(Appointment::toCsv).collect(Collectors.toList())); }

    private void writeLines(String path, List<String> lines) {
        try (var bw = new BufferedWriter(new FileWriter(path))) {
            for (String line : lines) { bw.write(line); bw.newLine(); }
        } catch (IOException e) {
            System.err.println("[DataStore] Write failed " + path + ": " + e.getMessage());
        }
    }

    private void loadAll() {
        loadDoctors();
        loadPatients();
        loadAppointments();
    }

    private void loadDoctors() {
        int maxId = 0;
        for (String line : readLines(DOCTORS_FILE)) {
            try {
                Doctor d = line.startsWith("S:")
                        ? com.clinicms.model.Specialist.fromCsv(line.substring(2))
                        : Doctor.fromCsv(line);
                doctors.add(d);
                if (d.getId() > maxId) maxId = d.getId();
            } catch (Exception ignored) {}
        }
        IdGenerator.seedDoctorId(maxId);
    }

    private void loadPatients() {
        int maxId = 0;
        for (String line : readLines(PATIENTS_FILE)) {
            try {
                Patient p = Patient.fromCsv(line);
                patients.add(p);
                if (p.getId() > maxId) maxId = p.getId();
            } catch (Exception ignored) {}
        }
        IdGenerator.seedPatientId(maxId);
    }

    private void loadAppointments() {
        int maxId = 0;
        for (String line : readLines(APPOINTMENTS_FILE)) {
            try {
                Appointment a = Appointment.fromCsv(line);
                appointments.add(a);
                if (a.getId() > maxId) maxId = a.getId();
            } catch (Exception ignored) {}
        }
        IdGenerator.seedAppointmentId(maxId);
    }

    private List<String> readLines(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return List.of();
        try (var br = new BufferedReader(new FileReader(filePath))) {
            return br.lines().map(String::trim).filter(l -> !l.isEmpty()).collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }
}
