package com.clinicms.controller;

import javafx.scene.control.Label;

public abstract class BaseController {

    protected void showStatus(Label lblStatus, String msg, boolean isError) {
        lblStatus.setText(msg);
        lblStatus.getStyleClass().removeAll("status-ok", "status-error");
        lblStatus.getStyleClass().add(isError ? "status-error" : "status-ok");
    }
}
