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
            stage.setResizable(false);
            //fix resizable adding extra size to window bug
            stage.sizeToScene();
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void showAlert(ResourceBundle stringBundle,String headerKey, String bodyKey) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(stringBundle.getString("genericAlertTitle"));
        alert.setHeaderText(stringBundle.getString(headerKey));
        alert.setContentText(stringBundle.getString(bodyKey));
        alert.show();
    }

}
