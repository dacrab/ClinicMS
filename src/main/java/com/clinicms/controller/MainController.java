package com.clinicms.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        showDoctors();
    }

    @FXML
    private void showDoctors() {
        loadView("/com/clinicms/fxml/DoctorView.fxml");
    }

    @FXML
    private void showPatients() {
        loadView("/com/clinicms/fxml/PatientView.fxml");
    }

    @FXML
    private void showAppointments() {
        loadView("/com/clinicms/fxml/AppointmentView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            var alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Αδυναμια φορτωσης οθονης: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
