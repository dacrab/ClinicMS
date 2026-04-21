module com.clinicms {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.clinicms to javafx.fxml;
    opens com.clinicms.controller to javafx.fxml;
    opens com.clinicms.model to javafx.base;

    exports com.clinicms;
    exports com.clinicms.controller;
    exports com.clinicms.model;
    exports com.clinicms.service;
    exports com.clinicms.util;
}
