package com.clinicms.controller;

import com.clinicms.model.Patient;
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
 * Controller for the Patients view (PatientView.fxml).
 * Supports full CRUD operations with live search.
 */
public class PatientController extends BaseController {

    @FXML private TableView<Patient>            patientTable;
    @FXML private TableColumn<Patient, String>  colId;
    @FXML private TableColumn<Patient, String>  colName;
    @FXML private TableColumn<Patient, String>  colDob;
    @FXML private TableColumn<Patient, String>  colGender;
    @FXML private TableColumn<Patient, String>  colPhone;
    @FXML private TableColumn<Patient, String>  colEmail;

    @FXML private TextField  tfSearch;
    @FXML private TextField  tfName;
    @FXML private TextField  tfDob;
    @FXML private ComboBox<String> cbGender;
    @FXML private TextField  tfPhone;
    @FXML private TextField  tfEmail;
    @FXML private TextArea   taMedicalHistory;
    @FXML private Label      lblStatus;
    @FXML private Button     btnSave;
    @FXML private Button     btnDelete;

    private final DataStore              store     = DataStore.getInstance();
    private final ObservableList<Patient> tableData = FXCollections.observableArrayList();
    private Patient selectedPatient = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        cbGender.setItems(FXCollections.observableArrayList("Ανδρας", "Γυναικα", "Αλλο"));

        patientTable.setItems(tableData);
        refreshTable(store.getPatients());

        patientTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> populateForm(selected));

        tfSearch.textProperty().addListener(
                (obs, old, val) -> refreshTable(store.searchPatients(val)));

        btnDelete.setDisable(true);
    }

    @FXML
    private void onSave() {
        String name    = tfName.getText().trim();
        String dob     = tfDob.getText().trim();
        String gender  = cbGender.getValue();
        String phone   = tfPhone.getText().trim();
        String email   = tfEmail.getText().trim();
        String history = taMedicalHistory.getText().trim();

        if (!Validator.notBlank(name)) { showStatus(lblStatus, "Το ονομα ειναι υποχρεωτικο.", true); return; }
        if (!Validator.isValidDate(dob)) { showStatus(lblStatus, "Η ημερομηνια πρεπει να ειναι ηη/ΜΜ/εεεε.", true); return; }
        if (gender == null) { showStatus(lblStatus, "Παρακαλω επιλεξτε φυλο.", true); return; }
        if (!Validator.isValidEmail(email)) { showStatus(lblStatus, "Μη εγκυρη διευθυνση email.", true); return; }
        if (!Validator.isValidPhone(phone)) { showStatus(lblStatus, "Μη εγκυρος αριθμος τηλεφωνου.", true); return; }

        try {
            if (selectedPatient == null) {
                Patient p = new Patient(IdGenerator.nextPatientId(), name, dob, gender, phone, email, history);
                store.addPatient(p);
                showStatus(lblStatus, "Ο ασθενης εγγραφηκε επιτυχως.", false);
            } else {
                selectedPatient.setName(name);
                selectedPatient.setDateOfBirth(dob);
                selectedPatient.setGender(gender);
                selectedPatient.setPhone(phone);
                selectedPatient.setEmail(email);
                selectedPatient.setMedicalHistory(history);
                store.updatePatient(selectedPatient);
                showStatus(lblStatus, "Ο ασθενης ενημερωθηκε επιτυχως.", false);
            }
            clearForm();
            refreshTable(store.getPatients());
        } catch (Exception e) {
            showStatus(lblStatus, "Σφαλμα: " + e.getMessage(), true);
        }
    }

    @FXML
    private void onDelete() {
        if (selectedPatient == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Διαγραφη ασθενη " + selectedPatient.getName() + ";",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Επιβεβαιωση Διαγραφης");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    store.deletePatient(selectedPatient.getId());
                    clearForm();
                    refreshTable(store.getPatients());
                    showStatus(lblStatus, "Ο ασθενης διαγραφηκε.", false);
                } catch (IllegalStateException ex) {
                    showStatus(lblStatus, ex.getMessage(), true);
                }
            }
        });
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    private void populateForm(Patient p) {
        selectedPatient = p;
        if (p == null) { clearForm(); return; }
        tfName.setText(p.getName());
        tfDob.setText(p.getDateOfBirth());
        cbGender.setValue(p.getGender());
        tfPhone.setText(p.getPhone());
        tfEmail.setText(p.getEmail());
        taMedicalHistory.setText(p.getMedicalHistory());
        btnSave.setText("Ενημερωση Ασθενη");
        btnDelete.setDisable(false);
        lblStatus.setText("");
    }

    private void clearForm() {
        selectedPatient = null;
        tfName.clear(); tfDob.clear(); tfPhone.clear(); tfEmail.clear();
        cbGender.setValue(null);
        taMedicalHistory.clear();
        btnSave.setText("Εγγραφη Ασθενη");
        btnDelete.setDisable(true);
        lblStatus.setText("");
        patientTable.getSelectionModel().clearSelection();
    }

    private void refreshTable(List<Patient> list) {
        tableData.setAll(list);
    }
}
