package sample;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    //Initializes a GridPane to show a window same from the table.
    private void initTableSample(GridPane timetableGrid,Sheet sheet,int rows,int columns){

        for (int i = 0 ; i < rows ; i++){
            Row row = sheet.getRow(i);
            for (int j = 0 ; j < columns ; j++){
                Cell cell = row.getCell(j);
                String data = cell == null ? "" : cell.toString();
                Label label = makeGridLabel(timetableGrid,new Label(data));
                timetableGrid.add(label,j,i);
            }

        }
    }

    //Unpacks all merged cells in the sheet.
    private void unpackMergedCells(Sheet sheet){
        //For each merged region...
        for (int i = 0 ; i < sheet.getNumMergedRegions(); i++){

            CellRangeAddress cellRange = sheet.getMergedRegion(i);

            int firstRow = cellRange.getFirstRow();
            int lastRow = cellRange.getLastRow();
            int firstColumn = cellRange.getFirstColumn();
            int lastColumn = cellRange.getLastColumn();

            //For each row that it spans across...
            for (int currentRow= firstRow;currentRow<=lastRow;currentRow++){
                //For each column that it spans across that is within that row...
                for (int currentColumn= firstColumn; currentColumn<=lastColumn;currentColumn++){
                    sheet.getRow(currentRow).getCell(currentColumn).setCellValue(
                            sheet.getRow(firstRow).getCell(firstColumn).toString());
                }
            }
        }

    }

    //Sets some properties on a label to make it suitable for the grid.
    private Label makeGridLabel(GridPane grid,Label label){
        label.setStyle("-fx-background-color: #FFC107;" +
                "-fx-max-width: infinity;" +
                "-fx-max-height: infinity"
                );
        label.setAlignment(Pos.CENTER);
        GridPane.setFillWidth(label,true);
        GridPane.setFillHeight(label,true);
        return label;

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
                Sheet sheet = timetable.getSheetAt(0);
                unpackMergedCells(sheet);
                initTableSample(tableSample,sheet,5,5);
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
