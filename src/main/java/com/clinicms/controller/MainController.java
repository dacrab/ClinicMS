package com.clinicms.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controller for the main application shell (MainView.fxml).
 * Manages navigation between the three sub-views:
 * Doctors, Patients, and Appointments.
 */
public class MainController {

    @FXML private StackPane contentArea;

    // ── Navigation handlers ────────────────────────────────────────────────────

    /** Loads the Dashboard overview into the content area. */
    @FXML
    private void showDashboard() {
        loadView("/com/clinicms/fxml/DashboardView.fxml");
    }

    /** Loads the Doctors view into the content area. */
    @FXML
    private void showDoctors() {
        loadView("/com/clinicms/fxml/DoctorView.fxml");
    }

    /** Loads the Patients view into the content area. */
    @FXML
    private void showPatients() {
        loadView("/com/clinicms/fxml/PatientView.fxml");
    }

    /** Loads the Appointments view into the content area. */
    @FXML
    private void showAppointments() {
        loadView("/com/clinicms/fxml/AppointmentView.fxml");
    }

    /** Loads the Reports view into the content area. */
    @FXML
    private void showReports() {
        loadView("/com/clinicms/fxml/ReportView.fxml");
    }

    // ── Initialisation ─────────────────────────────────────────────────────────

    /** Opens the Dashboard view on startup. */
    @FXML
    public void initialize() {
        showDashboard();
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            // Εμφάνιση μηνύματος σφάλματος στον χρήστη αν αποτύχει η φόρτωση view
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Αδυναμία φόρτωσης οθόνης: " + e.getMessage());
            alert.setTitle("Σφάλμα");
            alert.showAndWait();
        }
    }
}
