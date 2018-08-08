package com.yli.timetable_assistant;

import com.yli.timetable_assistant.res.Resources;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle bundle= ResourceBundle.getBundle(Resources.PATH);
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
