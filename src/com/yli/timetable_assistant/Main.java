package com.yli.timetable_assistant;

import com.yli.timetable_assistant.res.Resources;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.ResourceBundle;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) {
        FXUtils.openWindow("Timetable asssist",primaryStage,
                700, 500,
                getClass().getResource("sample.fxml"),
                ResourceBundle.getBundle(Resources.PATH),
                new Controller());

    }


    public static void main(String[] args) {
        launch(args);
    }


}
