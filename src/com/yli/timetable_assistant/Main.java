package com.yli.timetable_assistant;

import com.yli.timetable_assistant.utils.FXUtils;
import com.yli.timetable_assistant.res.Integers;
import com.yli.timetable_assistant.res.StringsBundle;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.ResourceBundle;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage)
    {

        //This contains localized strings that will be shown to the user.
        ResourceBundle strings = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());

        //Open the window
        FXUtils.openWindow(
                primaryStage,
                Integers.WINDOW_WIDTH,
                Integers.WINDOW_HEIGHT,
                MainController.class.getResource(MainController.FXML_PATH),
                strings,
                new MainController());

    }


    public static void main(String[] args) {
        launch(args);
    }


}
