package com.yli.timetable_assistant.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FXUtils {

    public static void openWindow(String windowName, Stage stage,
                           int width, int height,
                           URL FXMLResourceUrl, ResourceBundle bundle,
                           Object controller) {
        FXMLLoader loader = new FXMLLoader(FXMLResourceUrl,bundle);
        loader.setController(controller);
        try {
            Parent root = loader.load();
            stage.setTitle(windowName);
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void showInfoAlert(String header, String body) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Woah there!");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.show();
    }

}
