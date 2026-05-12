package com.clinicms.controller;

import com.clinicms.model.Appointment;
import com.clinicms.model.Doctor;
import com.clinicms.model.Patient;
import com.clinicms.service.DataStore;
import com.clinicms.util.IdGenerator;
import com.clinicms.util.Validator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

public class AppointmentController {

    @FXML private TableView<Appointment> apptTable;
    @FXML private TableColumn<Appointment, String> colId, colPatient, colDoctor, colDate, colTime, colReason, colStatus;
    @FXML private ComboBox<Patient> cbPatient;
    @FXML private ComboBox<Doctor> cbDoctor;
    @FXML private TextField tfDate, tfReason;
    @FXML private ComboBox<String> cbTimeSlot, cbStatusFilter;
    @FXML private TextArea taNotes;
    @FXML private Label lblStatus;
    @FXML private Button btnSchedule, btnComplete, btnCancel;

    private final DataStore store = DataStore.getInstance();
    private final ObservableList<Appointment> tableData = FXCollections.observableArrayList();
    private Appointment selected = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colPatient.setCellValueFactory(c -> new SimpleStringProperty(
                store.findPatientById(c.getValue().getPatientId()).map(Patient::getName).orElse("?")));
        colDoctor.setCellValueFactory(c -> new SimpleStringProperty(
                store.findDoctorById(c.getValue().getDoctorId()).map(d -> "Dr. " + d.getName()).orElse("?")));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate()));
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimeSlot()));
        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));

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

        apptTable.setItems(tableData);
        cbPatient.setItems(FXCollections.observableArrayList(store.getPatients()));
        cbDoctor.setItems(FXCollections.observableArrayList(store.getDoctors()));

        cbTimeSlot.setItems(FXCollections.observableArrayList(
                "08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30",
                "12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30",
                "16:00","16:30","17:00","17:30","18:00"));

        cbStatusFilter.setItems(FXCollections.observableArrayList("ΟΛΑ", "SCHEDULED", "COMPLETED", "CANCELLED"));
        cbStatusFilter.setValue("ΟΛΑ");
        cbStatusFilter.valueProperty().addListener((obs, old, val) -> refreshTable());

        apptTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> populateForm(sel));
        refreshTable();
        updateButtons(null);
    }

    @FXML
    private void onSchedule() {
        Patient patient = cbPatient.getValue();
        Doctor doctor = cbDoctor.getValue();
        String date = tfDate.getText().trim();
        String timeSlot = cbTimeSlot.getValue();
        String reason = tfReason.getText().trim();
        String notes = taNotes.getText().trim();

        if (patient == null) { setStatus("Παρακαλω επιλεξτε ασθενη.", true); return; }
        if (doctor == null) { setStatus("Παρακαλω επιλεξτε ιατρο.", true); return; }
        if (!Validator.isValidDate(date)) { setStatus("Η ημερομηνια πρεπει να ειναι ηη/ΜΜ/εεεε.", true); return; }
        if (timeSlot == null) { setStatus("Παρακαλω επιλεξτε ωρα.", true); return; }
        if (!Validator.notBlank(reason)) { setStatus("Η αιτια ειναι υποχρεωτικη.", true); return; }

        try {
            if (selected == null) {
                store.addAppointment(new Appointment(
                        IdGenerator.nextAppointmentId(), patient.getId(), doctor.getId(),
                        date, timeSlot, reason, Appointment.Status.SCHEDULED, notes));
                setStatus("Το ραντεβου προγραμματιστηκε.", false);
            } else {
                selected.setPatientId(patient.getId());
                selected.setDoctorId(doctor.getId());
                selected.setDate(date);
                selected.setTimeSlot(timeSlot);
                selected.setReason(reason);
                selected.setNotes(notes);
                store.updateAppointment(selected);
                setStatus("Το ραντεβου ενημερωθηκε.", false);
            }
            clearForm();
            refreshTable();
        } catch (IllegalStateException e) {
            setStatus(e.getMessage(), true);
        }
    }

    @FXML
    private void onComplete() {
        if (selected == null) return;
        store.completeAppointment(selected.getId());
        setStatus("Το ραντεβου ολοκληρωθηκε.", false);
        clearForm();
        refreshTable();
    }

    @FXML
    private void onCancel() {
        if (selected == null) return;
        var confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Ακυρωση ραντεβου #" + selected.getId() + ";", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                store.cancelAppointment(selected.getId());
                setStatus("Το ραντεβου ακυρωθηκε.", false);
                clearForm();
                refreshTable();
            }
        });
    }

    @FXML
    private void onClear() { clearForm(); }

    private void populateForm(Appointment a) {
        selected = a;
        updateButtons(a);
        if (a == null) { clearForm(); return; }
        store.findPatientById(a.getPatientId()).ifPresent(cbPatient::setValue);
        store.findDoctorById(a.getDoctorId()).ifPresent(cbDoctor::setValue);
        tfDate.setText(a.getDate());
        cbTimeSlot.setValue(a.getTimeSlot());
        tfReason.setText(a.getReason());
        taNotes.setText(a.getNotes());
        btnSchedule.setText("Ενημερωση Ραντεβου");
        lblStatus.setText("");
    }

    private void clearForm() {
        selected = null;
        cbPatient.setValue(null); cbDoctor.setValue(null);
        tfDate.clear(); cbTimeSlot.setValue(null);
        tfReason.clear(); taNotes.clear();
        btnSchedule.setText("Προγραμματισμος Ραντεβου");
        lblStatus.setText("");
        apptTable.getSelectionModel().clearSelection();
        updateButtons(null);
    }

    private void refreshTable() {
        String filter = cbStatusFilter.getValue();
        List<Appointment> all = store.getAppointments();
        if (!"ΟΛΑ".equals(filter) && filter != null) {
            try {
                Appointment.Status s = Appointment.Status.valueOf(filter);
                all = all.stream().filter(a -> a.getStatus() == s).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }
        tableData.setAll(all);
    }

    private void updateButtons(Appointment a) {
        boolean canAct = a != null && a.getStatus() == Appointment.Status.SCHEDULED;
        btnComplete.setDisable(!canAct);
        btnCancel.setDisable(!canAct);
    }

    private void setStatus(String msg, boolean error) {
        lblStatus.setText(msg);
        lblStatus.getStyleClass().removeAll("status-ok", "status-error");
        lblStatus.getStyleClass().add(error ? "status-error" : "status-ok");
    }
}
