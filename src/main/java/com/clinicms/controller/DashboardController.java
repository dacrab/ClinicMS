package com.clinicms.controller;

import com.clinicms.model.Appointment;
import com.clinicms.model.Doctor;
import com.clinicms.model.Patient;
import com.clinicms.service.DataStore;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for the Dashboard overview view (DashboardView.fxml).
 *
 * <p>Displays key statistics at a glance:
 * <ul>
 *   <li>Total counts of doctors, patients, appointments</li>
 *   <li>Breakdown of appointments by status</li>
 *   <li>Top 5 busiest doctors</li>
 *   <li>Most recent scheduled appointments</li>
 * </ul>
 */
public class DashboardController {

    // ── FXML bindings ──────────────────────────────────────────────────────────

    @FXML private Label lblTotalDoctors;
    @FXML private Label lblTotalPatients;
    @FXML private Label lblTotalAppointments;
    @FXML private Label lblScheduled;
    @FXML private Label lblCompleted;
    @FXML private Label lblCancelled;
    @FXML private VBox  vboxTopDoctors;
    @FXML private VBox  vboxRecentAppointments;

    // ── Dependencies ───────────────────────────────────────────────────────────

    private final DataStore store = DataStore.getInstance();

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    /**
     * Called automatically by JavaFX after the FXML is loaded.
     * Populates all statistics panels.
     */
    @FXML
    public void initialize() {
        populateSummaryCards();
        populateTopDoctors();
        populateRecentAppointments();
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /**
     * Populates the six summary-card labels with live data from the {@link DataStore}.
     */
    private void populateSummaryCards() {
        List<Doctor>      doctors      = store.getDoctors();
        List<Patient>     patients     = store.getPatients();
        List<Appointment> appointments = store.getAppointments();

        lblTotalDoctors.setText(String.valueOf(doctors.size()));
        lblTotalPatients.setText(String.valueOf(patients.size()));
        lblTotalAppointments.setText(String.valueOf(appointments.size()));

        long scheduled = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.SCHEDULED).count();
        long completed = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.COMPLETED).count();
        long cancelled = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.CANCELLED).count();

        lblScheduled.setText(String.valueOf(scheduled));
        lblCompleted.setText(String.valueOf(completed));
        lblCancelled.setText(String.valueOf(cancelled));
    }

    /**
     * Builds the "Top Doctors by appointment count" list and adds rows to {@code vboxTopDoctors}.
     */
    private void populateTopDoctors() {
        vboxTopDoctors.getChildren().clear();

        // Group appointments by doctorId, count them, sort descending, take top 5
        Map<Integer, Long> countByDoctor = store.getAppointments().stream()
                .collect(Collectors.groupingBy(Appointment::getDoctorId, Collectors.counting()));

        countByDoctor.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    String doctorName = store.findDoctorById(entry.getKey())
                            .map(d -> "Dr. " + d.getName())
                            .orElse("Unknown");
                    Label row = new Label("  " + doctorName + "  —  " + entry.getValue() + " ραντεβου");
                    row.getStyleClass().add("dash-list-item");
                    vboxTopDoctors.getChildren().add(row);
                });

        if (vboxTopDoctors.getChildren().isEmpty()) {
            vboxTopDoctors.getChildren().add(new Label("  Δεν υπαρχουν ραντεβου ακομα."));
        }
    }

    /**
     * Shows the 5 most recently added SCHEDULED appointments.
     */
    private void populateRecentAppointments() {
        vboxRecentAppointments.getChildren().clear();

        store.getAppointments().stream()
                .filter(a -> a.getStatus() == Appointment.Status.SCHEDULED)
                .sorted((a, b) -> Integer.compare(b.getId(), a.getId())) // newest first
                .limit(5)
                .forEach(a -> {
                    String patientName = store.findPatientById(a.getPatientId())
                            .map(Patient::getName).orElse("Unknown");
                    String doctorName  = store.findDoctorById(a.getDoctorId())
                            .map(d -> "Dr. " + d.getName()).orElse("Unknown");
                    Label row = new Label(
                            "  [" + a.getDate() + " " + a.getTimeSlot() + "]  "
                                    + patientName + "  →  " + doctorName);
                    row.getStyleClass().add("dash-list-item");
                    vboxRecentAppointments.getChildren().add(row);
                });

        if (vboxRecentAppointments.getChildren().isEmpty()) {
            vboxRecentAppointments.getChildren().add(new Label("  Δεν υπαρχουν προγραμματισμενα ραντεβου."));
        }
    }
}
