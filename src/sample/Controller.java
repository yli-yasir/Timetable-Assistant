package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    private GridPane tableSample;

    @FXML
    private Button browseButton;

    @FXML
    void initialize(){

        initBrowseButton(browseButton);
    }


    private void initTableSample(GridPane timetableGrid,Sheet sheet){
        for (int i = 0 ; i < 5 ; i++){
            Row row = sheet.getRow(i);
            for (int j = 0 ; j < 5 ; j++){
                Cell cell = row.getCell(j);
                String data = cell == null ? "" : cell.toString();
                Label label = new Label(data);
                label.setStyle("-fx-border-color: black");
                timetableGrid.add(label,j,i);
            }
        }
    }
    //Initializes a button to be used for browsing.
    private void initBrowseButton(Button b){

        b.setOnAction(event -> {

            FileChooser chooser = new FileChooser();

            chooser.setTitle("[Timetable Assistant] Please choose a file:");
            /*This method takes a Window object as an argument...
             *If the parent window is passed then it will not be able to
             * interact with it anymore.
             *
             * passing null is also valid however it will not produce the effect
             * above.
             */
            File file = chooser.showOpenDialog(browseButton.getScene().getWindow());

            Workbook timetable = readTimetable(file);

            if (timetable!=null) {
                initTableSample(tableSample,timetable.getSheetAt(0));
            }

        });
    }

    //WorkbookFactory.create(File f) wrapped in in try/catch.
    private Workbook readTimetable(File file){
        Workbook timetable = null ;
        try {
            timetable  = WorkbookFactory.create(file);
        }
        catch(IOException | InvalidFormatException e){
            e.printStackTrace();
        }
        return timetable;
    }




}
