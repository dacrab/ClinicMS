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

/** Controller για CRUD λειτουργιες ιατρων με αναζητηση σε πραγματικο χρονο. */
public class DoctorController {

    @FXML private TableView<Doctor> doctorTable;
    @FXML private TableColumn<Doctor, String> colId, colName, colSpecialty, colPhone, colEmail;
    @FXML private TextField tfSearch, tfName, tfSpecialty, tfPhone, tfEmail;
    @FXML private Label lblStatus;
    @FXML private Button btnSave, btnDelete;

    private final DataStore store = DataStore.getInstance();
    private final ObservableList<Doctor> tableData = FXCollections.observableArrayList();
    private Doctor selected = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSpecialty.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        doctorTable.setItems(tableData);
        refreshTable();

        doctorTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> populateForm(sel));
        tfSearch.textProperty().addListener((obs, old, val) -> tableData.setAll(store.searchDoctors(val)));
        btnDelete.setDisable(true);
    }

    @FXML
    private void onSave() {
        String name = tfName.getText().trim();
        String specialty = tfSpecialty.getText().trim();
        String phone = tfPhone.getText().trim();
        String email = tfEmail.getText().trim();

        if (!Validator.notBlank(name)) { setStatus("Το ονομα ειναι υποχρεωτικο.", true); return; }
        if (!Validator.notBlank(specialty)) { setStatus("Η ειδικοτητα ειναι υποχρεωτικη.", true); return; }
        if (!Validator.isValidEmail(email)) { setStatus("Μη εγκυρη διευθυνση email.", true); return; }
        if (!Validator.isValidPhone(phone)) { setStatus("Μη εγκυρος αριθμος τηλεφωνου.", true); return; }

        try {
            if (selected == null) {
                store.addDoctor(new Doctor(IdGenerator.nextDoctorId(), name, specialty, phone, email));
                setStatus("Ο ιατρος προστεθηκε.", false);
            } else {
                selected.setName(name);
                selected.setSpecialty(specialty);
                selected.setPhone(phone);
                selected.setEmail(email);
                store.updateDoctor(selected);
                setStatus("Ο ιατρος ενημερωθηκε.", false);
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
                "Διαγραφη ιατρου " + selected.getName() + ";", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    store.deleteDoctor(selected.getId());
                    clearForm();
                    refreshTable();
                    setStatus("Ο ιατρος διαγραφηκε.", false);
                } catch (IllegalStateException ex) {
                    setStatus(ex.getMessage(), true);
                }
            }
        });
    }

    @FXML
    private void onClear() { clearForm(); }

    private void populateForm(Doctor d) {
        selected = d;
        if (d == null) { clearForm(); return; }
        tfName.setText(d.getName());
        tfSpecialty.setText(d.getSpecialty());
        tfPhone.setText(d.getPhone());
        tfEmail.setText(d.getEmail());
        btnSave.setText("Ενημερωση Ιατρου");
        btnDelete.setDisable(false);
        lblStatus.setText("");
    }

    private void clearForm() {
        selected = null;
        tfName.clear(); tfSpecialty.clear(); tfPhone.clear(); tfEmail.clear();
        btnSave.setText("Προσθηκη Ιατρου");
        btnDelete.setDisable(true);
        lblStatus.setText("");
        doctorTable.getSelectionModel().clearSelection();
    }

    private void refreshTable() { tableData.setAll(store.getDoctors()); }

    private void setStatus(String msg, boolean error) {
        lblStatus.setText(msg);
        lblStatus.getStyleClass().removeAll("status-ok", "status-error");
        lblStatus.getStyleClass().add(error ? "status-error" : "status-ok");
    }
}
