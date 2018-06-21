package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

public class Controller {

    @FXML
    private Button browseButton;

    @FXML
    void initialize(){

        browseButton.setOnAction(event -> {

            FileChooser chooser = new FileChooser();

            chooser.setTitle("[Timetable Assistant] Please choose a file:");
            /*This method takes a Window object as an argument...
             *If the parent window is passed then it will not be able to
             * interact with it anymore.
             *
             * passing null is also valid however it will not produce the effect
             * above.
             */
            chooser.showOpenDialog(browseButton.getScene().getWindow());
        });
    }




}
