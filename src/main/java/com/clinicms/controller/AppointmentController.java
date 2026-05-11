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

/**
 * Controller for the Appointments view (AppointmentView.fxml).
 * Handles scheduling, updating, completing, and cancelling appointments,
 * with double-booking prevention enforced in {@link DataStore}.
 */
public class AppointmentController extends BaseController {

    @FXML private TableView<Appointment>                apptTable;
    @FXML private TableColumn<Appointment, String>      colId;
    @FXML private TableColumn<Appointment, String>      colPatient;
    @FXML private TableColumn<Appointment, String>      colDoctor;
    @FXML private TableColumn<Appointment, String>      colDate;
    @FXML private TableColumn<Appointment, String>      colTime;
    @FXML private TableColumn<Appointment, String>      colReason;
    @FXML private TableColumn<Appointment, String>      colStatus;

    @FXML private ComboBox<Patient>     cbPatient;
    @FXML private ComboBox<Doctor>      cbDoctor;
    @FXML private TextField             tfDate;
    @FXML private ComboBox<String>      cbTimeSlot;
    @FXML private TextField             tfReason;
    @FXML private TextArea              taNotes;
    @FXML private ComboBox<String>      cbStatusFilter;
    @FXML private Label                 lblStatus;
    @FXML private Button                btnSchedule;
    @FXML private Button                btnComplete;
    @FXML private Button                btnCancel;
    @FXML private Button                btnClear;

    private final DataStore                    store     = DataStore.getInstance();
    private final ObservableList<Appointment>  tableData = FXCollections.observableArrayList();
    private Appointment selectedAppointment = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getId())));

        colPatient.setCellValueFactory(c -> {
            int pid = c.getValue().getPatientId();
            return new SimpleStringProperty(
                    store.findPatientById(pid).map(Patient::getName).orElse("Unknown"));
        });

        colDoctor.setCellValueFactory(c -> {
            int did = c.getValue().getDoctorId();
            return new SimpleStringProperty(
                    store.findDoctorById(did).map(d -> "Dr. " + d.getName()).orElse("Unknown"));
        });

        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate()));
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimeSlot()));
        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("status-scheduled", "status-completed", "status-cancelled");
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    switch (item) {
                        case "SCHEDULED"  -> getStyleClass().add("status-scheduled");
                        case "COMPLETED"  -> getStyleClass().add("status-completed");
                        case "CANCELLED"  -> getStyleClass().add("status-cancelled");
                    }
                }
            }
        });

        apptTable.setItems(tableData);

        cbPatient.setItems(FXCollections.observableArrayList(store.getPatients()));
        cbDoctor.setItems(FXCollections.observableArrayList(store.getDoctors()));

        cbTimeSlot.setItems(FXCollections.observableArrayList(
                "08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30",
                "12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30",
                "16:00","16:30","17:00","17:30","18:00"
        ));

        cbStatusFilter.setItems(FXCollections.observableArrayList("ΟΛΑ", "SCHEDULED", "COMPLETED", "CANCELLED"));
        cbStatusFilter.setValue("ΟΛΑ");
        cbStatusFilter.valueProperty().addListener((obs, old, val) -> applyFilter(val));

        apptTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> populateForm(sel));

        refreshTable("ALL");
        updateActionButtons(null);
    }

    @FXML
    private void onSchedule() {
        Patient patient  = cbPatient.getValue();
        Doctor  doctor   = cbDoctor.getValue();
        String  date     = tfDate.getText().trim();
        String  timeSlot = cbTimeSlot.getValue();
        String  reason   = tfReason.getText().trim();
        String  notes    = taNotes.getText().trim();

        if (patient == null) { showStatus(lblStatus, "Παρακαλω επιλεξτε ασθενη.", true); return; }
        if (doctor == null)  { showStatus(lblStatus, "Παρακαλω επιλεξτε ιατρο.", true); return; }
        if (!Validator.isValidDate(date)) { showStatus(lblStatus, "Η ημερομηνια πρεπει να ειναι ηη/ΜΜ/εεεε.", true); return; }
        if (timeSlot == null) { showStatus(lblStatus, "Παρακαλω επιλεξτε ωρα.", true); return; }
        if (!Validator.notBlank(reason)) { showStatus(lblStatus, "Η αιτια ειναι υποχρεωτικη.", true); return; }

        try {
            if (selectedAppointment == null) {
                Appointment a = new Appointment(
                        IdGenerator.nextAppointmentId(),
                        patient.getId(), doctor.getId(),
                        date, timeSlot, reason,
                        Appointment.Status.SCHEDULED, notes);
                store.addAppointment(a);
                showStatus(lblStatus, "Το ραντεβου προγραμματιστηκε επιτυχως.", false);
            } else {
                selectedAppointment.setPatientId(patient.getId());
                selectedAppointment.setDoctorId(doctor.getId());
                selectedAppointment.setDate(date);
                selectedAppointment.setTimeSlot(timeSlot);
                selectedAppointment.setReason(reason);
                selectedAppointment.setNotes(notes);
                store.updateAppointment(selectedAppointment);
                showStatus(lblStatus, "Το ραντεβου ενημερωθηκε επιτυχως.", false);
            }
            clearForm();
            refreshTable(cbStatusFilter.getValue());
        } catch (IllegalStateException e) {
            showStatus(lblStatus, e.getMessage(), true);
        }
    }

    @FXML
    private void onComplete() {
        if (selectedAppointment == null) return;
        store.completeAppointment(selectedAppointment.getId());
        showStatus(lblStatus, "Το ραντεβου ολοκληρωθηκε.", false);
        clearForm();
        refreshTable(cbStatusFilter.getValue());
    }

    @FXML
    private void onCancel() {
        if (selectedAppointment == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Ακυρωση ραντεβου #" + selectedAppointment.getId() + ";",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Επιβεβαιωση Ακυρωσης");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                store.cancelAppointment(selectedAppointment.getId());
                showStatus(lblStatus, "Το ραντεβου ακυρωθηκε.", false);
                clearForm();
                refreshTable(cbStatusFilter.getValue());
            }
        });
    }

    @FXML
    private void onClear() {
        clearForm();
    }

    private void populateForm(Appointment a) {
        selectedAppointment = a;
        updateActionButtons(a);
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
        selectedAppointment = null;
        cbPatient.setValue(null);
        cbDoctor.setValue(null);
        tfDate.clear();
        cbTimeSlot.setValue(null);
        tfReason.clear();
        taNotes.clear();
        btnSchedule.setText("Προγραμματισμος Ραντεβου");
        lblStatus.setText("");
        apptTable.getSelectionModel().clearSelection();
        updateActionButtons(null);
    }

    private void applyFilter(String filter) {
        refreshTable(filter);
    }

    private void refreshTable(String filter) {
        List<Appointment> all = store.getAppointments();
        if (!"ΟΛΑ".equals(filter) && filter != null) {
            try {
                Appointment.Status s = Appointment.Status.valueOf(filter);
                all = all.stream().filter(a -> a.getStatus() == s).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }
        tableData.setAll(all);
    }

    private void updateActionButtons(Appointment a) {
        boolean hasScheduled = a != null && a.getStatus() == Appointment.Status.SCHEDULED;
        btnComplete.setDisable(!hasScheduled);
        btnCancel.setDisable(!hasScheduled);
        btnSchedule.setDisable(false);
    }
}
