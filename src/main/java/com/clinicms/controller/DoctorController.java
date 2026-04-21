package com.clinicms.controller;

import com.clinicms.model.Doctor;
import com.clinicms.service.DataStore;
import com.clinicms.util.IdGenerator;
import com.clinicms.util.Validator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller for the Doctors view (DoctorView.fxml).
 * Supports full CRUD operations on doctors with real-time search.
 */
public class DoctorController {

    // ── FXML fields ────────────────────────────────────────────────────────────

    @FXML private TableView<Doctor>            doctorTable;
    @FXML private TableColumn<Doctor, String>  colId;
    @FXML private TableColumn<Doctor, String>  colName;
    @FXML private TableColumn<Doctor, String>  colSpecialty;
    @FXML private TableColumn<Doctor, String>  colPhone;
    @FXML private TableColumn<Doctor, String>  colEmail;

    @FXML private TextField  tfSearch;
    @FXML private TextField  tfName;
    @FXML private TextField  tfSpecialty;
    @FXML private TextField  tfPhone;
    @FXML private TextField  tfEmail;
    @FXML private Label      lblStatus;
    @FXML private Button     btnSave;
    @FXML private Button     btnDelete;
    @FXML private Button     btnClear;

    // ── State ──────────────────────────────────────────────────────────────────

    private final DataStore           store    = DataStore.getInstance();
    private final ObservableList<Doctor> tableData = FXCollections.observableArrayList();
    private Doctor selectedDoctor = null;

    // ── Initialisation ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Wire up table columns
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSpecialty.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        doctorTable.setItems(tableData);
        refreshTable(store.getDoctors());

        // Row selection → populate form
        doctorTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> populateForm(selected));

        // Live search
        tfSearch.textProperty().addListener(
                (obs, old, val) -> refreshTable(store.searchDoctors(val)));

        btnDelete.setDisable(true);
    }

    // ── Event handlers ─────────────────────────────────────────────────────────

    /** Saves (add or update) a doctor from the form fields. */
    @FXML
    private void onSave() {
        String name      = tfName.getText().trim();
        String specialty = tfSpecialty.getText().trim();
        String phone     = tfPhone.getText().trim();
        String email     = tfEmail.getText().trim();

        // Validation
        if (!Validator.notBlank(name)) {
            showStatus("Το ονομα ειναι υποχρεωτικο.", true);
            return;
        }
        if (!Validator.notBlank(specialty)) {
            showStatus("Η ειδικοτητα ειναι υποχρεωτικη.", true);
            return;
        }
        if (!Validator.isValidEmail(email)) {
            showStatus("Μη εγκυρη διευθυνση email.", true);
            return;
        }
        if (!Validator.isValidPhone(phone)) {
            showStatus("Μη εγκυρος αριθμος τηλεφωνου.", true);
            return;
        }

        try {
            if (selectedDoctor == null) {
                Doctor d = new Doctor(IdGenerator.nextDoctorId(), name, specialty, phone, email);
                store.addDoctor(d);
                showStatus("Ο ιατρος προστεθηκε επιτυχως.", false);
            } else {
                selectedDoctor.setName(name);
                selectedDoctor.setSpecialty(specialty);
                selectedDoctor.setPhone(phone);
                selectedDoctor.setEmail(email);
                store.updateDoctor(selectedDoctor);
                showStatus("Ο ιατρος ενημερωθηκε επιτυχως.", false);
            }
            clearForm();
            refreshTable(store.getDoctors());
        } catch (Exception e) {
            showStatus("Σφαλμα: " + e.getMessage(), true);
        }
    }

    /** Deletes the currently selected doctor. */
    @FXML
    private void onDelete() {
        if (selectedDoctor == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Διαγραφη ιατρου " + selectedDoctor.getName() + ";",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Επιβεβαιωση Διαγραφης");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    store.deleteDoctor(selectedDoctor.getId());
                    clearForm();
                    refreshTable(store.getDoctors());
                    showStatus("Ο ιατρος διαγραφηκε.", false);
                } catch (IllegalStateException ex) {
                    showStatus(ex.getMessage(), true);
                }
            }
        });
    }

    /** Clears the form and deselects any table row. */
    @FXML
    private void onClear() {
        clearForm();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void populateForm(Doctor d) {
        selectedDoctor = d;
        if (d == null) {
            clearForm();
            return;
        }
        tfName.setText(d.getName());
        tfSpecialty.setText(d.getSpecialty());
        tfPhone.setText(d.getPhone());
        tfEmail.setText(d.getEmail());
        btnSave.setText("Ενημερωση Ιατρου");
        btnDelete.setDisable(false);
        lblStatus.setText("");
    }

    private void clearForm() {
        selectedDoctor = null;
        tfName.clear();
        tfSpecialty.clear();
        tfPhone.clear();
        tfEmail.clear();
        btnSave.setText("Προσθηκη Ιατρου");
        btnDelete.setDisable(true);
        lblStatus.setText("");
        doctorTable.getSelectionModel().clearSelection();
    }

    private void refreshTable(List<Doctor> list) {
        tableData.setAll(list);
    }

    private void showStatus(String msg, boolean isError) {
        lblStatus.setText(msg);
        lblStatus.getStyleClass().removeAll("status-ok", "status-error");
        lblStatus.getStyleClass().add(isError ? "status-error" : "status-ok");
    }
}
