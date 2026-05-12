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

/** Controller για CRUD λειτουργιες ασθενων με αναζητηση σε πραγματικο χρονο. */
public class PatientController {

    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, String> colId, colName, colDob, colGender, colPhone, colEmail;
    @FXML private TextField tfSearch, tfName, tfDob, tfPhone, tfEmail;
    @FXML private ComboBox<String> cbGender;
    @FXML private TextArea taMedicalHistory;
    @FXML private Label lblStatus;
    @FXML private Button btnSave, btnDelete;

    private final DataStore store = DataStore.getInstance();
    private final ObservableList<Patient> tableData = FXCollections.observableArrayList();
    private Patient selected = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        cbGender.setItems(FXCollections.observableArrayList("Ανδρας", "Γυναικα", "Αλλο"));
        patientTable.setItems(tableData);
        refreshTable();

        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> populateForm(sel));
        tfSearch.textProperty().addListener((obs, old, val) -> tableData.setAll(store.searchPatients(val)));
        btnDelete.setDisable(true);
    }

    @FXML
    private void onSave() {
        String name = tfName.getText().trim();
        String dob = tfDob.getText().trim();
        String gender = cbGender.getValue();
        String phone = tfPhone.getText().trim();
        String email = tfEmail.getText().trim();
        String history = taMedicalHistory.getText().trim();

        if (!Validator.notBlank(name)) { setStatus("Το ονομα ειναι υποχρεωτικο.", true); return; }
        if (!Validator.isValidDate(dob)) { setStatus("Η ημερομηνια πρεπει να ειναι ηη/ΜΜ/εεεε.", true); return; }
        if (gender == null) { setStatus("Παρακαλω επιλεξτε φυλο.", true); return; }
        if (!Validator.isValidEmail(email)) { setStatus("Μη εγκυρη διευθυνση email.", true); return; }
        if (!Validator.isValidPhone(phone)) { setStatus("Μη εγκυρος αριθμος τηλεφωνου.", true); return; }

        try {
            if (selected == null) {
                store.addPatient(new Patient(IdGenerator.nextPatientId(), name, dob, gender, phone, email, history));
                setStatus("Ο ασθενης εγγραφηκε.", false);
            } else {
                selected.setName(name);
                selected.setDateOfBirth(dob);
                selected.setGender(gender);
                selected.setPhone(phone);
                selected.setEmail(email);
                selected.setMedicalHistory(history);
                store.updatePatient(selected);
                setStatus("Ο ασθενης ενημερωθηκε.", false);
            }
            clearForm();
            refreshTable();
        } catch (Exception e) {
            setStatus(e.getMessage(), true);
        }
    }

    @FXML
    private void onDelete() {
        if (selected == null) return;
        var confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Διαγραφη ασθενη " + selected.getName() + ";", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    store.deletePatient(selected.getId());
                    clearForm();
                    refreshTable();
                    setStatus("Ο ασθενης διαγραφηκε.", false);
                } catch (IllegalStateException ex) {
                    setStatus(ex.getMessage(), true);
                }
            }
        });
    }

    @FXML
    private void onClear() { clearForm(); }

    private void populateForm(Patient p) {
        selected = p;
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
        selected = null;
        tfName.clear(); tfDob.clear(); tfPhone.clear(); tfEmail.clear();
        cbGender.setValue(null);
        taMedicalHistory.clear();
        btnSave.setText("Εγγραφη Ασθενη");
        btnDelete.setDisable(true);
        lblStatus.setText("");
        patientTable.getSelectionModel().clearSelection();
    }

    private void refreshTable() { tableData.setAll(store.getPatients()); }

    private void setStatus(String msg, boolean error) {
        lblStatus.setText(msg);
        lblStatus.getStyleClass().removeAll("status-ok", "status-error");
        lblStatus.getStyleClass().add(error ? "status-error" : "status-ok");
    }
}
