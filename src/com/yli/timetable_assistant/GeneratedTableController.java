package com.yli.timetable_assistant;

import com.yli.timetable_assistant.res.StringsBundle;
import com.yli.timetable_assistant.table.Course;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;



public class GeneratedTableController {

    static final String FXML_PATH = "/com/yli/timetable_assistant/res/generatedTable.fxml";

    @FXML
    private TableView<Course> table;

    //The map we are going to draw the table from.
    private ObservableList<Course> courses;

    private ResourceBundle stringsBundle = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());


    GeneratedTableController(ObservableList<Course> courses){

        this.courses = courses;
    }

    @FXML
    private void initialize(){
        populateTableView();
    }

    void populateTableView(){
        table.setItems(courses);

        ArrayList<String> addedDays = new ArrayList<>();
        for (Course course : courses){
            String day = course.getDay().toString();
            if (!addedDays.contains(day)) {
                TableColumn<Course, String> courseDayCol = new TableColumn<>(day);
                table.getColumns().add(courseDayCol);
                addedDays.add(day);
            }
        }


    }
    private FileChooser makeSaveDialog(){
        FileChooser saveDialog = new FileChooser();
        saveDialog.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG", "*.png"));
        return saveDialog;
    }

    /*Since we are saving, there should be only one extension in the list, so
        we just get the one at the first index, and extensions should be specified as
        *.<extension> so we trim the in string starting from the second index until the end
        to get a string extension which can be used in saving the file*/
    private String getExtension(FileChooser saveDialog){
        return saveDialog.getSelectedExtensionFilter().getExtensions()
                .get(0).substring(2);
    }

    private void saveImage(BufferedImage image,File file,String extension){
        try{
            ImageIO.write(image,extension,file);
        }
        catch(IOException e ){
            e.printStackTrace();
        }

    }

}
