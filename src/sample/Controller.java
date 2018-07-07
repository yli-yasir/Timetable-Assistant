package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;


public class Controller {

    private Sheet timetableSheet;

    private StepButton currentlySelectedButton;
    @FXML
    private Label instructionLabel;
    @FXML
    private GridPane tableSample;
    @FXML
    private GridPane courseSelectionGrid;
    @FXML
    private Button browseButton;
    @FXML
    private TilePane tableSampleControls;

    private ChoiceBox<Integer> fontSizeOptions;

    private TextField outputFileNameField;

    @FXML
    private void initialize() {
        initCourseSelectionGrid();
        initSelectionStepControlBar();
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
        });

        //Buttons which will be used in this pane.
        Button searchButton = new Button("Search");
        Button generateButton = new Button("Generate timetable");
        searchButton.setOnAction(event ->
                TableUtils.search(timetableSheet, searchResultList, field.getText()));
        generateButton.setOnAction(event ->
        TableUtils.generateTimetable(timetableSheet,addedCoursesList));
        generateButton.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);

        //SettingsBox
        VBox settingsBox = new VBox();
        settingsBox.setPadding(new Insets(0,0,0,20));
        settingsBox.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        Label outputFileNameLabel = new Label("File name:");
        Label settingsLabel = new Label("Settings:");
        Label fontSizeLabel = new Label("Font size:");

        Insets  sectionHeaderMargin = new Insets(10,0,0,10);
        Insets sectionBodyMargin =  new Insets(0,0,0,10);
        outputFileNameField = new TextField();
        outputFileNameField.setPromptText("eg: Student No");
         fontSizeOptions= new ChoiceBox<>();
        ObservableList<Integer> sizeList = FXCollections.observableArrayList();
        for(int i = 1 ; i <=72;i++ ){
            sizeList.add(i);
        }
        fontSizeOptions.setItems(sizeList);
        fontSizeOptions.setValue(12);

        VBox.setMargin(outputFileNameLabel,sectionHeaderMargin);
        VBox.setMargin(outputFileNameField,sectionBodyMargin);
        VBox.setMargin(fontSizeLabel,sectionHeaderMargin);
        VBox.setMargin(fontSizeOptions,sectionBodyMargin);

        settingsBox.getChildren().add(settingsLabel);
        settingsBox.getChildren().add(outputFileNameLabel);
        settingsBox.getChildren().add(outputFileNameField);
        settingsBox.getChildren().add(fontSizeLabel);
        settingsBox.getChildren().add(fontSizeOptions);

        courseSelectionGrid.add(field, 0, 0);
        courseSelectionGrid.add(searchButton, 1, 0);
        courseSelectionGrid.add(availableCoursesHeader, 0, 1);
        courseSelectionGrid.add(addedCoursesHeader, 1, 1);
        courseSelectionGrid.add(availableCourses, 0, 2);
        courseSelectionGrid.add(addedCourses, 1, 2);
        courseSelectionGrid.add(generateButton, 0, 3,2,1);
        courseSelectionGrid.add(settingsBox, 2, 1,1,2);


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

                    if (currentlySelectedButton != null) {
                        int rowIndex = GridPane.getRowIndex(label);
                        int columnIndex = GridPane.getColumnIndex(label);
                        SelectionStep selectionStep = currentlySelectedButton.getStep();

                        if (selectionStep == SelectionStep.SELECT_COURSE) {
                            TableUtils.selectionMetaMap.putCourseMetaData(rowIndex);
                        } else {
                            try {
                                TableUtils.selectionMetaMap.putOtherMetaData(
                                        rowIndex, columnIndex, selectionStep
                                );
                            } catch (ExampleCourseNotSetException e) {
                                //todo add popup indicating whats wrong
                                e.printStackTrace();
                            }

                        }

                        currentlySelectedButton.setText(label.getText());
                    }
                });

            }

        }
    }

    /*Initializes a VBox with controls which will be used to choose example data
      from the sample table*/
    private void initSelectionStepControlBar() {

        for (SelectionStep step : SelectionStep.values()) {
            StepButton button = new StepButton(step.title(), step);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnMouseClicked(event -> {
                currentlySelectedButton = button;
                instructionLabel.setText(step.decription());

            });
            tableSampleControls.getChildren().add(button);
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
        label.setStyle("-fx-background-color: #FFC312;" +
                "-fx-max-width: infinity;" +
                "-fx-max-height: infinity"
        );
        label.setAlignment(Pos.CENTER);
        GridPane.setFillWidth(label, true);
        GridPane.setFillHeight(label, true);
        return label;

    }


}
