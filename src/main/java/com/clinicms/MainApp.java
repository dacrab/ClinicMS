package com.clinicms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Clinic Management System JavaFX application.
 *
 * <p>Loads the main dashboard FXML and applies the global stylesheet.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/clinicms/fxml/MainView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1150, 740);
            scene.getStylesheets().add(
                    getClass().getResource("/com/clinicms/css/style.css").toExternalForm());

            primaryStage.setTitle("Συστημα Διαχειρισης Κλινικης – CN5004");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(950);
            primaryStage.setMinHeight(650);
            primaryStage.show();
        } catch (Exception e) {
            // Κρίσιμο σφάλμα εκκίνησης – εμφάνιση και τερματισμός
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Αδυναμία εκκίνησης εφαρμογής:\n" + e.getMessage());
            alert.setTitle("Κρίσιμο Σφάλμα");
            alert.showAndWait();
            javafx.application.Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
