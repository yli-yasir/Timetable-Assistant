package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;


public class Controller {

    private Sheet timetableSheet;

    private StepLabel currentlySelectedLabel;
    @FXML
    private Label instructionLabel;
    @FXML
    private GridPane tableSample;
    @FXML
    private GridPane courseSelectionGrid;
    @FXML
    private Button browseButton;
    @FXML
    private VBox tableSampleControlVBox;


    @FXML
    private void initialize() {
        initCourseSelectionGrid();
        initTableSampleControlVBox();
        initBrowseButton();
    }


    //Initializes controls that are related to selecting and adding courses.
    private void initCourseSelectionGrid() {

        //This will be used to enter a search query.
        TextField field = new TextField();
        field.setFocusTraversable(false);
        field.setPromptText("Course name");


        /*Label and list for showing courses that have been added from
        the list that contains available courses.*/
        Label addedCoursesHeader = new Label("Added:");
        ListView<String> addedCourses = new ListView<>();
        ObservableList<String> addedCoursesList = FXCollections.observableArrayList();
        addedCourses.setItems(addedCoursesList);
        addedCourses.setOnMouseClicked(event ->
                addedCoursesList
                        .remove(addedCourses.getSelectionModel().getSelectedIndex()));

        //Label and list for displaying courses that are in the table.
        Label availableCoursesHeader = new Label("Available:");
        ListView<String> availableCourses = new ListView<>();
        ObservableList<String> searchResultList = FXCollections.observableArrayList();
        availableCourses.setItems(
                searchResultList);
        availableCourses.setOnMouseClicked(event -> {
                    String clickedItem = availableCourses.getSelectionModel().getSelectedItem();
                    if (!addedCoursesList.contains(clickedItem)) {
                        addedCoursesList.add(clickedItem
                        );
                    }
                } );

        //Buttons which will be used in this pane.
        Button searchButton = new Button("Search");
        Button generateButton = new Button("Generate timetable");
        searchButton.setOnAction(event ->
                TableUtils.search(timetableSheet, searchResultList, field.getText()));
        generateButton.setOnAction(event -> TableUtils.generateTimetable());


        courseSelectionGrid.add(field, 0, 0);
        courseSelectionGrid.add(searchButton, 1, 0);
        courseSelectionGrid.add(availableCoursesHeader, 0, 1);
        courseSelectionGrid.add(addedCoursesHeader, 1, 1);
        courseSelectionGrid.add(availableCourses, 0, 2);
        courseSelectionGrid.add(addedCourses, 1, 2);
        courseSelectionGrid.add(generateButton, 0, 3);

    }


    /*Initializes a GridPane to show a small part from the timetable, in which
    the user can select a course and it's corresponding data as an example
    to the program.*/
    @SuppressWarnings("SameParameterValue")
    private void initTableSample(Sheet sheet, int rows, int columns) {

        for (int i = 0; i < rows; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < columns; j++) {
                Cell cell = row.getCell(j);
                String data = cell == null ? "" : cell.toString();
                Label label = makeGridLabel(new Label(data));
                tableSample.add(label, j, i);

                label.setOnMouseClicked(event -> {

                    if (currentlySelectedLabel != null) {
                        TableUtils.exampleCoords.put(currentlySelectedLabel.getStep(),
                                new CellCoords(GridPane.getRowIndex(label),GridPane.getColumnIndex(label)));
                        currentlySelectedLabel.setText(label.getText());
                    }
                });

            }

        }
    }

    /*Initializes a VBox with controls which will be used to choose example data
      from the sample table*/
    private void initTableSampleControlVBox() {

        for (SelectionStep step : SelectionStep.values()) {
            StepLabel label = new StepLabel(step.title(), step);
            label.setOnMouseClicked(event -> {
                currentlySelectedLabel = label;
                instructionLabel.setText(step.decription());

            });
            tableSampleControlVBox.getChildren().add(label);
        }


    }


    //todo handle file being null
    //todo handle user closing browsing window (file = null?)
    //todo preferably make the file opening process in a background thread
    //todo display a loading bar while the file is being opened.
    //Initializes a button to be used for browsing.
    private void initBrowseButton() {

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
            File file = chooser.showOpenDialog(browseButton.getScene().getWindow());

            Workbook timetable = TableUtils.readTimetable(file);

            if (timetable != null) {
                timetableSheet = timetable.getSheetAt(0);
                TableUtils.unpackMergedCells(timetableSheet);
                initTableSample(timetableSheet, 5, 5);
            }

        });
    }

    //Sets some properties on a label to make it suitable for the grid.
    private Label makeGridLabel(Label label) {
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
