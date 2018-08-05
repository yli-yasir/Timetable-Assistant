package com.yli.timetable_assistant;

import com.yli.timetable_assistant.example_selection.ExampleCourseNotSetException;
import com.yli.timetable_assistant.example_selection.SelectionMode;
import com.yli.timetable_assistant.example_selection.SelectionModeButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.util.ResourceBundle;

//todo separate strings in view from code.
//todo separate style of views from code.
//todo separate classes into proper packages.

//todo center text in table cell


public class Controller {

    private Sheet timetableSheet;

    private SelectionModeButton currentlySelectedButton;

    @FXML
    private HBox browseBar;
    @FXML
    private Label instructionLabel;
    @FXML
    private GridPane tableSample;
    @FXML
    private GridPane courseSelectionGrid;


    @FXML
    private HBox exampleSelectionBar;

    private ChoiceBox<Integer> fontSizeOptions;



    private TextField outputFileNameField;

    private boolean isReadyToSearch;

    private ResourceBundle bundle = ResourceBundle.getBundle("com.yli.timetable_assistant.res.Resources");

    @FXML
    private void initialize() {
        //populateBrowseBar();
        populateExampleSelectionControlBar();
        populateCourseSelectionGrid();
    }


    //Populates the browse bar with appropriate controls.
    private void populateBrowseBar(){
        Label label = new Label(bundle.getString("windowSize"));
        Label x = new Label("X");
        ChoiceBox<Integer> exampleWindowRows= new ChoiceBox<>();
        ChoiceBox<Integer> exampleWindowColumns = new ChoiceBox<>();
        ObservableList<Integer> choices = FXCollections.observableArrayList();
        for (int i= 1; i < 50;i++){
            choices.add(i);
        }
        exampleWindowRows.setValue(5);
        exampleWindowColumns.setValue(5);
        exampleWindowRows.setItems(choices);
        exampleWindowColumns.setItems(choices);
        Button button = new Button(bundle.getString("browseButton"));
        initBrowseButton(button,exampleWindowRows,exampleWindowColumns);
        browseBar.getChildren().addAll(label,exampleWindowRows,x,exampleWindowColumns,button);
    }

    //todo preferably make the file opening process in a background thread
    //todo display a loading bar while the file is being opened.
    //Initializes a button to be used for browsing.
    private Button initBrowseButton(Button button,ChoiceBox<Integer> exampleWindowRows,ChoiceBox<Integer> exampleWindowColumns) {

        button.setOnAction(event -> {
            FileChooser chooser = new FileChooser();

            chooser.setTitle("[Timetable Assistant] Please choose a file:");
            /*This method takes a Window object as an argument...
             *If the parent window is passed then it will not be able to
             * interact with it anymore.
             *
             * passing null is also valid however it will not produce the effect
             * above.
             */
            File file = chooser.showOpenDialog(button.getScene().getWindow());

            Workbook timetable = TableManager.readTimetable(file);

            if (timetable != null) {
                timetableSheet = timetable.getSheetAt(0);
                TableManager.unpackMergedCells(timetableSheet);
                initTableSample(timetableSheet, 5, 5);
                button.setText("File: " + file.getName());
            }
        });
        return button;
    }

    //Initializes controls that are related to selecting and adding courses.
    private void populateCourseSelectionGrid() {

        //This will be used to enter a search query.
        TextField field = new TextField();
        field.setPromptText(bundle.getString("searchFieldPrompt"));

        /*Label and list for showing courses that have been added from
        the list that contains available courses.*/
        Label addedCoursesHeader = new Label(bundle.getString("addedCoursesHeader"));
        addedCoursesHeader.getStyleClass().add("Header");

        ListView<String> addedCourses = new ListView<>();
        ObservableList<String> addedCoursesList = FXCollections.observableArrayList();
        addedCourses.setItems(addedCoursesList);

        addedCourses.setOnMouseClicked(event ->{
            MultipleSelectionModel<String> sModel = addedCourses.getSelectionModel();
            String string = sModel.getSelectedItem();
        if (string !=null)
                addedCoursesList
                        .remove(sModel.getSelectedIndex());});

        //Label and list for displaying courses that are in the table.
        Label availableCoursesHeader = new Label(bundle.getString("availableCoursesHeader"));
        availableCoursesHeader.getStyleClass().add("Header");
        ListView<String> availableCourses = new ListView<>();
        ObservableList<String> searchResultList = FXCollections.observableArrayList();
        availableCourses.setItems(
                searchResultList);

        availableCourses.setOnMouseClicked(event -> {
            String clickedItem = availableCourses.getSelectionModel().getSelectedItem();
            if (!addedCoursesList.contains(clickedItem) && clickedItem!=null) {
                addedCoursesList.add(clickedItem
                );
            }
        });

        //Buttons which will be used in this pane.
        Button searchButton = new Button(bundle.getString("searchButton"));
        Button generateButton = new Button(bundle.getString("generateButton"));

        searchButton.setOnAction(event ->{
            if (isReadyToSearch){
                TableManager.search(timetableSheet, searchResultList, field.getText());}
                else{
                showInfoAlert(bundle.getString("insufficientInfoHeader"),bundle.getString("insufficientInfoBody"));
            }
        });

        generateButton.setOnAction(event ->{
            String fileName = outputFileNameField.getText();
            if (!addedCoursesList.isEmpty() && fileName!=null &&
                    !fileName.isEmpty() )
            TableManager.generateTimetable(timetableSheet, addedCoursesList,fontSizeOptions.getValue(),fileName);
                else
                    showInfoAlert(bundle.getString("notReadyToSearchHeader"),bundle.getString("notReadyToSearchBody"));});


        //SettingsBox
        Label settingsHeader = new Label(bundle.getString("settingsHeader"));
        settingsHeader.getStyleClass().add("Header");


        VBox settingsBox = new VBox();
        settingsBox.getStyleClass().add("SettingsBox");
        GridPane.setHgrow(settingsBox,Priority.ALWAYS);

        Label outputFileNameLabel = new Label(bundle.getString("outputFileName"));
        outputFileNameField = new TextField();
        outputFileNameField.setPromptText(bundle.getString("outputFileNameFieldPrompt"));

        Label fontSizeLabel = new Label(bundle.getString("fontSize"));
        fontSizeOptions = new ChoiceBox<>();
        ObservableList<Integer> sizeList = FXCollections.observableArrayList();
        for (int i = 1; i <= 72; i++) sizeList.add(i);
        fontSizeOptions.setItems(sizeList);
        fontSizeOptions.setValue(12);

        settingsBox.getChildren().addAll(outputFileNameLabel,
                outputFileNameField,
                fontSizeLabel,
                fontSizeOptions);


        courseSelectionGrid.add(field, 0, 0);
        courseSelectionGrid.add(searchButton, 1, 0);
        courseSelectionGrid.add(availableCoursesHeader, 0, 1);
        courseSelectionGrid.add(addedCoursesHeader, 1, 1);
        courseSelectionGrid.add(availableCourses, 0, 2);
        courseSelectionGrid.add(addedCourses, 1, 2);
        courseSelectionGrid.add(generateButton, 0, 3,3,1);
        courseSelectionGrid.add(settingsHeader,2,1);
        courseSelectionGrid.add(settingsBox, 2, 2);

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
                        com.yli.timetable_assistant.example_selection.SelectionMode selectionMode = currentlySelectedButton.getStep();

                        if (selectionMode == com.yli.timetable_assistant.example_selection.SelectionMode.SELECT_COURSE) {
                            TableManager.selectionModeToDataMap.puSelectionModeData(rowIndex);
                        } else {
                            try {
                                TableManager.selectionModeToDataMap.putCourseInfoMetaData(
                                        rowIndex, columnIndex, selectionMode
                                );
                            } catch (ExampleCourseNotSetException e) {
                                e.printStackTrace();
                            }
                        }
                        //todo this is text related to view.
                        String instruction;

                        if (TableManager.selectionModeToDataMap.size() < com.yli.timetable_assistant.example_selection.SelectionMode.values().length) {
                            instruction = "Please choose the remaining information...";
                        } else {
                            instruction = "All done! You can search for and add courses now!";
                            isReadyToSearch=true;
                        }

                        instructionLabel.setText(instruction);
                        currentlySelectedButton.setText(selectionMode.prefix() + label.getText());
                        currentlySelectedButton = null;
                    }
                });

            }

        }
    }

    /*populates with controls which will be used to choose example data
      from the sample table*/
    private void populateExampleSelectionControlBar() {

        ObservableList<Node> children = exampleSelectionBar.getChildren();


        for (SelectionMode step : SelectionMode.values()) {
            SelectionModeButton button = new SelectionModeButton(step.title(), step);
            HBox.setHgrow(button,Priority.ALWAYS);
            /*The listener merely changes the currently selected button further action
                is handled in the table sample control that will be clicked.*/
            button.setOnMouseClicked(event -> {
                if (timetableSheet == null) {
                    showInfoAlert("File not chosen yet!",
                            "Please choose a file first!");
                } else if (button.getStep() != com.yli.timetable_assistant.example_selection.SelectionMode.SELECT_COURSE &&
                        !TableManager.selectionModeToDataMap.containsKey(SelectionMode.SELECT_COURSE)) {
                    showInfoAlert("Course not chosen yet!",
                            "Please choose a course first!");
                } else {
                    currentlySelectedButton = button;
                    instructionLabel.setText(step.description());
                }
            });
            children.add(button);
        }
    }




    //Sets some properties on a label to make it suitable for the grid.
    private Label makeGridLabel(Label label) {
        label.setStyle("-fx-background-color: #FFC312;" +
                "-fx-max-width: infinity;" +
                "-fx-max-height: infinity;"
        );
        label.setAlignment(Pos.CENTER);
        GridPane.setFillWidth(label, true);
        GridPane.setFillHeight(label, true);
        return label;

    }

    private void showInfoAlert(String header, String body) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Woah there!");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.show();
    }
}
