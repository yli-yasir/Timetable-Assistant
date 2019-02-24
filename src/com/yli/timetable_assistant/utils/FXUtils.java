package com.yli.timetable_assistant.utils;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXUtils {

    public static void openWindow(Stage stage,
                           int width, int height,
                           URL FXMLResourceUrl, ResourceBundle strings,
                           Object controller) {

        //make a loader and point it to the fxml resource and strings.
        FXMLLoader loader = new FXMLLoader(FXMLResourceUrl,strings);
        //register the loader with the controller.
        loader.setController(controller);
        //attempt to open the window
        try {
            //ask the loader to load its resources
            Parent root = loader.load();
            //set the title of the window
            stage.setTitle(strings.getString("appName"));
            //make a scene
            Scene scene = new Scene(root, width, height);
            //Stages contain scenes, register the scene we made with our stage
            stage.setScene(scene);
            //give a bit of opacity to the stage (cuz it looks nice :) )
            stage.setOpacity(0.95);
//            //-------------disable resizing------------------------
//            stage.setResizable(false);
//            //fix resizable adding extra size to window bug
//            stage.sizeToScene();
//            //-----------------------------------------------------
            //maximize the stage
            //stage.setMaximized(true);

            //and finally show the stage
            stage.show();
        }
        //throw an exception if we fail to load
        catch(IOException e){
            e.printStackTrace();
        }
    }

    //Makes an error alert...
    public static void showErrorAlert(ResourceBundle stringBundle, String headerKey, String bodyKey) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(stringBundle.getString("genericAlertTitle"));
        alert.setHeaderText(stringBundle.getString(headerKey));
        alert.setContentText(stringBundle.getString(bodyKey));
        alert.show();
    }


    //browse for a file.
    public static File browseFile(Window parentWindow) {
        //New file chooser obj.
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));

        return chooser.showOpenDialog(parentWindow);
    }

    //remove an item from a list view
    public static void removeItem(ListView<String> removingFrom) {
        MultipleSelectionModel<String> sModel = removingFrom.getSelectionModel();
        String string = sModel.getSelectedItem();
        if (string != null)
            removingFrom.getItems()
                    .remove(sModel.getSelectedIndex());
    }

    //Handle adding an item from a list view to a list of another.
    public static void addItem(ListView<String> addingFrom, ObservableList<String> addingTo) {
        String clickedItem = addingFrom.getSelectionModel().getSelectedItem();
        if (!addingTo.contains(clickedItem) && clickedItem != null) {
            addingTo.add(clickedItem
            );
        }
    }
}
