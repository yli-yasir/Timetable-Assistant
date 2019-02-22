package com.yli.timetable_assistant;

import com.yli.timetable_assistant.fx.FXUtils;
import com.yli.timetable_assistant.res.Dimensions;
import com.yli.timetable_assistant.res.StringsBundle;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.ResourceBundle;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage)
    {
        ResourceBundle strings = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());

        FXUtils.openWindow(strings.getString("appName"),
                primaryStage,
                Dimensions.WINDOW_WIDTH,
                Dimensions.WINDOW_HEIGHT,
                MainController.class.getResource(MainController.FXML_PATH),
                strings,
                new MainController());
    }


    public static void main(String[] args) {
        launch(args);
    }


}
