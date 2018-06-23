package sample;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;

import java.io.File;


public class Controller {
    @FXML
    private GridPane tableSample;

    @FXML
    private Button browseButton;

    @FXML
    private VBox exampleVBox;

    private
    @FXML
    void initialize() {
        initExampleVBox(exampleVBox);
        initBrowseButton(browseButton);
    }

    //Initializes a GridPane to show a window same from the table.
    private void initTableSample(GridPane timetableGrid, Sheet sheet, int rows, int columns) {

        for (int i = 0; i < rows; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < columns; j++) {
                Cell cell = row.getCell(j);
                String data = cell == null ? "" : cell.toString();
                Label label = makeGridLabel(timetableGrid, new Label(data));
                timetableGrid.add(label, j, i);
            }

        }
    }

    //Initializes a VBox to be used for setting example data.
    private void initExampleVBox(VBox exampleVBox) {

        for (SelectionStep step: SelectionStep.values()){
            exampleVBox.getChildren().add(new StepLabel(step.title(),step));

        }


    }


    //Initializes a button to be used for browsing.
    private void initBrowseButton(Button b) {

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

            Workbook timetable = TableUtils.readTimetable(file);

            if (timetable != null) {
                Sheet sheet = timetable.getSheetAt(0);
                TableUtils.unpackMergedCells(sheet);
                initTableSample(tableSample, sheet, 5, 5);
            }

        });
    }

    //Sets some properties on a label to make it suitable for the grid.
    private Label makeGridLabel(GridPane grid, Label label) {
        label.setStyle("-fx-background-color: #FFC107;" +
                "-fx-max-width: infinity;" +
                "-fx-max-height: infinity"
        );
        label.setAlignment(Pos.CENTER);
        GridPane.setFillWidth(label, true);
        GridPane.setFillHeight(label, true);
        return label;

    }


}
