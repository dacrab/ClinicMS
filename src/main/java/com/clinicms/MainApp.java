package com.clinicms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/clinicms/fxml/MainView.fxml"));
            Scene scene = new Scene(root, 1150, 740);
            scene.getStylesheets().add(getClass().getResource("/com/clinicms/css/style.css").toExternalForm());
            primaryStage.setTitle("Συστημα Διαχειρισης Κλινικης – CN5004");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(950);
            primaryStage.setMinHeight(650);
            primaryStage.show();
        } catch (Exception e) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Αδυναμια εκκινησης: " + e.getMessage()).showAndWait();
            javafx.application.Platform.exit();
        }
    }

    public static void main(String[] args) { launch(args); }
}
