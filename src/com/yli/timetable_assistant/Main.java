package com.yli.timetable_assistant;

import com.yli.timetable_assistant.fx.FXUtils;
import com.yli.timetable_assistant.res.Strings;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.ResourceBundle;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage)
    {

        FXUtils.openWindow("Timetable asssist",primaryStage,
                700, 500,
                MainController.class.getResource(MainController.FXML_PATH),
                ResourceBundle.getBundle(Strings.PATH),
                new MainController());

    }


    public static void main(String[] args) {
        launch(args);
    }


}
