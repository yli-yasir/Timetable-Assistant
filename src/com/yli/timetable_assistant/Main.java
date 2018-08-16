package com.yli.timetable_assistant;

import com.yli.timetable_assistant.fx.FXUtils;
import com.yli.timetable_assistant.res.IntsBundle;
import com.yli.timetable_assistant.res.StringsBundle;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.ResourceBundle;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage)
    {
        ResourceBundle stringBundle = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());
        IntsBundle intBundle = (IntsBundle) ResourceBundle.getBundle(IntsBundle.class.getCanonicalName());
        FXUtils.openWindow(stringBundle.getString("appName"),primaryStage,
                intBundle.getInteger("windowWidth"),intBundle.getInteger("windowHeight"),
                MainController.class.getResource(MainController.FXML_PATH),
                stringBundle,
                new MainController());
    }


    public static void main(String[] args) {
        launch(args);
    }


}
