package com.yli.timetable_assistant;

import com.yli.timetable_assistant.res.Resources;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle bundle= ResourceBundle.getBundle(Resources.path);
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"),bundle);
        primaryStage.setTitle("Timetable Assistant");
        Scene scene = new Scene(root,700,500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
