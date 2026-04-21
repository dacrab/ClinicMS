package com.clinicms.controller;

import com.clinicms.model.Appointment;
import com.clinicms.model.Doctor;
import com.clinicms.model.Patient;
import com.clinicms.service.DataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the Reports view (ReportView.fxml).
 *
 * <p>Provides a filterable, full-history view of all appointments,
 * allowing the user to filter by:
 * <ul>
 *   <li>Appointment status (ALL / SCHEDULED / COMPLETED / CANCELLED)</li>
 *   <li>Doctor name (free-text search)</li>
 *   <li>Patient name (free-text search)</li>
 * </ul>
 * Results can also be exported to a plain-text report file.
 */
public class ReportController {

    // ── FXML bindings ──────────────────────────────────────────────────────────

    @FXML private TableView<Appointment>             reportTable;
    @FXML private TableColumn<Appointment, String>   colId;
    @FXML private TableColumn<Appointment, String>   colPatient;
    @FXML private TableColumn<Appointment, String>   colDoctor;
    @FXML private TableColumn<Appointment, String>   colSpecialty;
    @FXML private TableColumn<Appointment, String>   colDate;
    @FXML private TableColumn<Appointment, String>   colTime;
    @FXML private TableColumn<Appointment, String>   colReason;
    @FXML private TableColumn<Appointment, String>   colStatus;
    @FXML private TableColumn<Appointment, String>   colNotes;

    @FXML private ComboBox<String> cbStatusFilter;
    @FXML private TextField        tfDoctorFilter;
    @FXML private TextField        tfPatientFilter;
    @FXML private Label            lblCount;
    @FXML private Label            lblExportStatus;

    // ── State ──────────────────────────────────────────────────────────────────

    private final DataStore                   store     = DataStore.getInstance();
    private final ObservableList<Appointment> tableData = FXCollections.observableArrayList();

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    /**
     * Initialises the report table columns, filter controls, and populates with all data.
     */
    @FXML
    public void initialize() {
        // Wire columns
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getId())));

        colPatient.setCellValueFactory(c -> new SimpleStringProperty(
                store.findPatientById(c.getValue().getPatientId())
                        .map(Patient::getName).orElse("Unknown")));

        colDoctor.setCellValueFactory(c -> new SimpleStringProperty(
                store.findDoctorById(c.getValue().getDoctorId())
                        .map(d -> "Dr. " + d.getName()).orElse("Unknown")));

        colSpecialty.setCellValueFactory(c -> new SimpleStringProperty(
                store.findDoctorById(c.getValue().getDoctorId())
                        .map(Doctor::getSpecialty).orElse("-")));

        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate()));
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimeSlot()));
        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        colNotes.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNotes()));

        // Status cell coloring
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("status-scheduled", "status-completed", "status-cancelled");
                if (empty || item == null) { setText(null); return; }
                setText(item);
                switch (item) {
                    case "SCHEDULED" -> getStyleClass().add("status-scheduled");
                    case "COMPLETED" -> getStyleClass().add("status-completed");
                    case "CANCELLED" -> getStyleClass().add("status-cancelled");
                }
            }
        });

        reportTable.setItems(tableData);

        // Filter controls
        cbStatusFilter.setItems(FXCollections.observableArrayList(
                "ΟΛΑ", "SCHEDULED", "COMPLETED", "CANCELLED"));
        cbStatusFilter.setValue("ΟΛΑ");

        cbStatusFilter.valueProperty().addListener((o, ov, nv) -> applyFilters());
        tfDoctorFilter.textProperty().addListener((o, ov, nv) -> applyFilters());
        tfPatientFilter.textProperty().addListener((o, ov, nv) -> applyFilters());

        applyFilters();
    }

    // ── Event handlers ─────────────────────────────────────────────────────────

    /**
     * Exports the currently visible appointments to {@code data/report.txt}.
     */
    @FXML
    private void onExport() {
        List<Appointment> visible = List.copyOf(tableData);
        StringBuilder sb = new StringBuilder();
        sb.append("=== ΣΥΣΤΗΜΑ ΔΙΑΧΕΙΡΙΣΗΣ ΚΛΙΝΙΚΗΣ – ΑΝΑΦΟΡΑ ΡΑΝΤΕΒΟΥ ===\n");
        sb.append(String.format("Generated: %s | Records: %d%n%n",
                java.time.LocalDateTime.now().toString().replace("T", " ").substring(0, 16),
                visible.size()));
        sb.append(String.format("%-5s %-22s %-22s %-14s %-6s %-20s %-10s%n",
                "ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status"));
        sb.append("-".repeat(105)).append("\n");

        for (Appointment a : visible) {
            String patientName = store.findPatientById(a.getPatientId())
                    .map(Patient::getName).orElse("Unknown");
            String doctorName  = store.findDoctorById(a.getDoctorId())
                    .map(d -> "Dr. " + d.getName()).orElse("Unknown");
            sb.append(String.format("%-5d %-22s %-22s %-14s %-6s %-20s %-10s%n",
                    a.getId(), truncate(patientName, 20), truncate(doctorName, 20),
                    a.getDate(), a.getTimeSlot(), truncate(a.getReason(), 18),
                    a.getStatus().name()));
        }

        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(
                new java.io.FileWriter("data/report.txt"))) {
            bw.write(sb.toString());
            lblExportStatus.setText("Η αναφορα εξαχθηκε στο data/report.txt");
            lblExportStatus.getStyleClass().removeAll("status-error");
            lblExportStatus.getStyleClass().add("status-ok");
        } catch (java.io.IOException e) {
            lblExportStatus.setText("Αποτυχια εξαγωγης: " + e.getMessage());
            lblExportStatus.getStyleClass().add("status-error");
        }
    }

    /** Clears all filter fields. */
    @FXML
    private void onClearFilters() {
        cbStatusFilter.setValue("ALL");
        tfDoctorFilter.clear();
        tfPatientFilter.clear();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Applies all three filters (status, doctor name, patient name) and refreshes the table.
     */
    private void applyFilters() {
        String statusFilter  = cbStatusFilter.getValue();
        String doctorFilter  = tfDoctorFilter.getText().toLowerCase().trim();
        String patientFilter = tfPatientFilter.getText().toLowerCase().trim();

        List<Appointment> filtered = store.getAppointments().stream()
                .filter(a -> "ΟΛΑ".equals(statusFilter) || a.getStatus().name().equals(statusFilter))
                .filter(a -> {
                    if (doctorFilter.isEmpty()) return true;
                    return store.findDoctorById(a.getDoctorId())
                            .map(d -> d.getName().toLowerCase().contains(doctorFilter))
                            .orElse(false);
                })
                .filter(a -> {
                    if (patientFilter.isEmpty()) return true;
                    return store.findPatientById(a.getPatientId())
                            .map(p -> p.getName().toLowerCase().contains(patientFilter))
                            .orElse(false);
                })
                .collect(Collectors.toList());

        tableData.setAll(filtered);
        lblCount.setText("Εμφανιση " + filtered.size() + " εγγραφων");
    }

    /** Truncates a string to the given max length for report formatting. */
    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
