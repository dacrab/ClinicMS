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

/**
 * Singleton service (Data Access Object pattern) that manages all in-memory
 * entity collections and persists changes to CSV files on every write.
 *
 * <p><strong>Design:</strong> Uses the Singleton pattern so there is exactly
 * one shared instance across all JavaFX controllers, ensuring a consistent
 * view of the data.
 *
 * <p><strong>Persistence strategy:</strong> On every add/update/delete the
 * entire collection is re-written to the corresponding CSV file.  On startup
 * all three files are read back into memory so data survives application
 * restarts.
 *
 * <p><strong>Files created in {@code data/}:</strong>
 * <ul>
 *   <li>{@code doctors.csv}      – plain {@link com.clinicms.model.Doctor} lines, Specialist lines prefixed {@code S:}</li>
 *   <li>{@code patients.csv}     – {@link com.clinicms.model.Patient} lines</li>
 *   <li>{@code appointments.csv} – {@link com.clinicms.model.Appointment} lines</li>
 * </ul>
 *
 * <p><strong>Double-booking prevention:</strong> {@link #addAppointment} and
 * {@link #updateAppointment} call {@link #checkDoubleBooking} which throws
 * {@link IllegalStateException} if a SCHEDULED conflict is detected for the
 * same doctor, date and time slot.
 */
public class DataStore {

    // ── Singleton ──────────────────────────────────────────────────────────────

    private static DataStore instance;

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // ── File paths ─────────────────────────────────────────────────────────────

    private static final String DATA_DIR          = "data";
    private static final String DOCTORS_FILE      = DATA_DIR + "/doctors.csv";
    private static final String PATIENTS_FILE     = DATA_DIR + "/patients.csv";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "/appointments.csv";

    // ── In-memory collections ──────────────────────────────────────────────────

    private final List<Doctor>      doctors      = new ArrayList<>();
    private final List<Patient>     patients     = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();

    // ── Constructor ────────────────────────────────────────────────────────────

    private DataStore() {
        ensureDataDirectory();
        loadAll();
    }

    // ── Public API: Doctors ────────────────────────────────────────────────────

    /** Returns an unmodifiable snapshot of all doctors. */
    public List<Doctor> getDoctors() {
        return List.copyOf(doctors);
    }

    /** Adds a new doctor and persists the change. */
    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        saveDoctors();
    }

    /** Updates an existing doctor (matched by ID) and persists. */
    public void updateDoctor(Doctor updated) {
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getId() == updated.getId()) {
                doctors.set(i, updated);
                break;
            }
        }
        saveDoctors();
    }

    /**
     * Deletes a doctor by ID.
     *
     * @throws IllegalStateException if the doctor has any SCHEDULED appointments
     */
    public void deleteDoctor(int id) {
        boolean hasScheduled = appointments.stream()
                .anyMatch(a -> a.getDoctorId() == id
                        && a.getStatus() == Appointment.Status.SCHEDULED);
        if (hasScheduled) {
            throw new IllegalStateException(
                    "Cannot delete doctor with active (SCHEDULED) appointments.");
        }
        doctors.removeIf(d -> d.getId() == id);
        saveDoctors();
    }

    /** Finds a doctor by ID. */
    public Optional<Doctor> findDoctorById(int id) {
        return doctors.stream().filter(d -> d.getId() == id).findFirst();
    }

    /** Returns doctors whose name or specialty contains the query (case-insensitive). */
    public List<Doctor> searchDoctors(String query) {
        String q = query.toLowerCase();
        return doctors.stream()
                .filter(d -> d.getName().toLowerCase().contains(q)
                        || d.getSpecialty().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ── Public API: Patients ───────────────────────────────────────────────────

    public List<Patient> getPatients() {
        return List.copyOf(patients);
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
        savePatients();
    }

    public void updatePatient(Patient updated) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId() == updated.getId()) {
                patients.set(i, updated);
                break;
            }
        }
        savePatients();
    }

    /**
     * Deletes a patient by ID.
     *
     * @throws IllegalStateException if the patient has any SCHEDULED appointments
     */
    public void deletePatient(int id) {
        boolean hasScheduled = appointments.stream()
                .anyMatch(a -> a.getPatientId() == id
                        && a.getStatus() == Appointment.Status.SCHEDULED);
        if (hasScheduled) {
            throw new IllegalStateException(
                    "Cannot delete patient with active (SCHEDULED) appointments.");
        }
        patients.removeIf(p -> p.getId() == id);
        savePatients();
    }

    public Optional<Patient> findPatientById(int id) {
        return patients.stream().filter(p -> p.getId() == id).findFirst();
    }

    public List<Patient> searchPatients(String query) {
        String q = query.toLowerCase();
        return patients.stream()
                .filter(p -> p.getName().toLowerCase().contains(q)
                        || p.getPhone().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ── Public API: Appointments ───────────────────────────────────────────────

    public List<Appointment> getAppointments() {
        return List.copyOf(appointments);
    }

    /**
     * Schedules a new appointment, checking for double-bookings first.
     *
     * @throws IllegalStateException if the doctor already has a SCHEDULED appointment at that slot
     */
    public void addAppointment(Appointment appointment) {
        checkDoubleBooking(appointment, -1);
        appointments.add(appointment);
        saveAppointments();
    }

    /**
     * Updates an existing appointment (matched by ID), re-checking for double-bookings.
     *
     * @throws IllegalStateException on double-booking conflict
     */
    public void updateAppointment(Appointment updated) {
        checkDoubleBooking(updated, updated.getId());
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId() == updated.getId()) {
                appointments.set(i, updated);
                break;
            }
        }
        saveAppointments();
    }

    /** Cancels an appointment (sets status to CANCELLED) and persists. */
    public void cancelAppointment(int id) {
        appointments.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .ifPresent(a -> a.setStatus(Appointment.Status.CANCELLED));
        saveAppointments();
    }

    /** Marks an appointment as COMPLETED and persists. */
    public void completeAppointment(int id) {
        appointments.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .ifPresent(a -> a.setStatus(Appointment.Status.COMPLETED));
        saveAppointments();
    }



    // ── Double-booking guard ───────────────────────────────────────────────────

    /**
     * Throws if the given appointment would conflict with any existing SCHEDULED appointment,
     * excluding the appointment with {@code excludeId} (used when updating).
     */
    private void checkDoubleBooking(Appointment incoming, int excludeId) {
        if (incoming.getStatus() == Appointment.Status.CANCELLED) return;
        boolean conflict = appointments.stream()
                .filter(a -> a.getId() != excludeId)
                .anyMatch(a -> a.conflictsWith(incoming));
        if (conflict) {
            throw new IllegalStateException(
                    "The selected doctor already has a SCHEDULED appointment on "
                            + incoming.getDate() + " at " + incoming.getTimeSlot() + ".");
        }
    }

    // ── Persistence: Save ──────────────────────────────────────────────────────

    private void saveDoctors() {
        writeLines(DOCTORS_FILE, doctors.stream().map(Doctor::toCsv).collect(Collectors.toList()));
    }

    private void savePatients() {
        writeLines(PATIENTS_FILE, patients.stream().map(Patient::toCsv).collect(Collectors.toList()));
    }

    private void saveAppointments() {
        writeLines(APPOINTMENTS_FILE, appointments.stream().map(Appointment::toCsv).collect(Collectors.toList()));
    }

    private void writeLines(String filePath, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[DataStore] Failed to write " + filePath + ": " + e.getMessage());
        }
    }

    // ── Persistence: Load ──────────────────────────────────────────────────────

    private void loadAll() {
        loadDoctors();
        loadPatients();
        loadAppointments();
    }

    private void loadDoctors() {
        List<String> lines = readLines(DOCTORS_FILE);
        int maxId = 0;
        for (String line : lines) {
            try {
                Doctor d;
                if (line.startsWith("S:")) {
                    d = com.clinicms.model.Specialist.fromCsv(line.substring(2));
                } else {
                    d = Doctor.fromCsv(line);
                }
                doctors.add(d);
                if (d.getId() > maxId) maxId = d.getId();
            } catch (Exception e) {
                System.err.println("[DataStore] Skipping bad doctor line: " + line);
            }
        }
        IdGenerator.seedDoctorId(maxId);
    }

    private void loadPatients() {
        List<String> lines = readLines(PATIENTS_FILE);
        int maxId = 0;
        for (String line : lines) {
            try {
                Patient p = Patient.fromCsv(line);
                patients.add(p);
                if (p.getId() > maxId) maxId = p.getId();
            } catch (Exception e) {
                System.err.println("[DataStore] Skipping bad patient line: " + line);
            }
        }
        IdGenerator.seedPatientId(maxId);
    }

    private void loadAppointments() {
        List<String> lines = readLines(APPOINTMENTS_FILE);
        int maxId = 0;
        for (String line : lines) {
            try {
                Appointment a = Appointment.fromCsv(line);
                appointments.add(a);
                if (a.getId() > maxId) maxId = a.getId();
            } catch (Exception e) {
                System.err.println("[DataStore] Skipping bad appointment line: " + line);
            }
        }
        IdGenerator.seedAppointmentId(maxId);
    }

    private List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("[DataStore] Failed to read " + filePath + ": " + e.getMessage());
        }
        return lines;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
