package com.yli.timetable_assistant;

import com.yli.timetable_assistant.example_selection.IncorrectExampleInfoException;
import com.yli.timetable_assistant.fx.FXUtils;
import com.yli.timetable_assistant.res.IntsBundle;
import com.yli.timetable_assistant.res.StringsBundle;
import com.yli.timetable_assistant.table.DayToCourseListMap;
import com.yli.timetable_assistant.table.TableUtils;
import com.yli.timetable_assistant.tasks.TableReadTask;
import com.yli.timetable_assistant.example_selection.ExampleCourseNotSetException;
import com.yli.timetable_assistant.example_selection.SelectionMode;
import com.yli.timetable_assistant.buttons.SelectionModeButton;
import com.yli.timetable_assistant.example_selection.SelectionModeToDataMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.util.HashMap;
import java.util.ResourceBundle;

import static com.yli.timetable_assistant.fx.FXUtils.showInfoAlert;


//todo handle if user enters a sample window size bigger than the excel table.

public class MainController implements TableReadTask.TaskCallbacks<Workbook> {

    //Relative path for the FXML for this controller.
    static final String FXML_PATH = "/com/yli/timetable_assistant/res/main.fxml";

    //The sheet that contains the timetable.
    private Sheet timetableSheet;

    //The selection mode button that's currently selected.
    private SelectionModeButton currentSelectionModeButton;

    //A label to guide the user through the example selection process.
    @FXML
    private Label instructionLabel;

    //Contains buttons that have to do with selecting example course info.
    @FXML
    private HBox exampleSelectionControlBar;

    private Button browseButton;

    /*A grid which will be populated with labels which represent cells
    from the sheet*/
    @FXML
    private GridPane tableSample;

    /*A grid which contains all controls other than the ones
     that have to do with example selection.*/
    @FXML
    private GridPane controlGrid;

    //Progress indicator which will be shown or hidden at loading.
    @FXML
    private ProgressIndicator progressIndicator;

    /*Choice boxes to select the number of cols and rows which will be
    shown in the table sample grid.*/
    private ChoiceBox<Integer> windowRowsChoiceBox = new ChoiceBox<>();
    private ChoiceBox<Integer> windowColumnsChoiceBox = new ChoiceBox<>();

    /*Used to check if the user has completed providing example info before
    searching*/
    private boolean isReadyToSearch;

    //This will hold data selected in a certain mode
    private static SelectionModeToDataMap selectionModeToDataMap = new SelectionModeToDataMap();

    //Bundle which has string resources that will be used in the GUI.
    private ResourceBundle bundle = ResourceBundle.getBundle("com.yli.timetable_assistant.res.StringsBundle");

    /*This will be automatically called after injecting the variables above
    with their values*/
    @FXML
    private void initialize() {
        populateExampleSelectionControlBar();
        populateControlGrid();
    }

    /*populates with controls which will be used to choose example data
  from the sample table*/
    private void populateExampleSelectionControlBar() {

        //Get a ref to the children since we will be adding to them repeatedly.
        ObservableList<Node> children = exampleSelectionControlBar.getChildren();

        //Add the browse button first.
        addBrowseButton(children);

        //Add the selection mode buttons.
        addSelectionModeButtons(children);
    }

    //Set up and add browse button.
    private void addBrowseButton(ObservableList<Node> children) {
        browseButton = new Button(bundle.getString("browseButton"));
        setBrowseOnActionListener(browseButton);
        HBox.setHgrow(browseButton, Priority.ALWAYS);
        children.add(browseButton);
    }

    //Set up and add the selection mode buttons.
    private void addSelectionModeButtons(ObservableList<Node> children) {
        //Add as many buttons as needed for selection modes, in here I am making
        //a button for each mode.
        HashMap<SelectionMode,String> modeToName = new HashMap<>();
        modeToName.put(SelectionMode.SELECT_COURSE,bundle.getString("selectCourseModeName"));
        modeToName.put(SelectionMode.SELECT_TIME,bundle.getString("selectTimeModeName"));
        modeToName.put(SelectionMode.SELECT_HALL,bundle.getString("selectHallModeName"));
        modeToName.put(SelectionMode.SELECT_DAY,bundle.getString("selectDayModeName"));

        for (SelectionMode mode : SelectionMode.values()) {
            SelectionModeButton button = new SelectionModeButton(mode,modeToName.get(mode));
            HBox.setHgrow(button, Priority.ALWAYS);
            /*The listener merely changes the currently selected button further action
                is handled in the table sample control that will be clicked.*/
            setChangeModeOnActionListener(button);

            children.add(button);
        }
    }


    //Handle action for browse button.
    private void setBrowseOnActionListener(Button button) {

        button.setOnAction(event -> {

            //New file chooser obj.
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX","*.xlsx"));

            /*This method takes a Window object as an argument...
             *If the parent window is passed then it will not be able to
             * interact with it anymore.
             *
             * passing null is also valid however it will not produce the effect
             * above.
             */
            File file = chooser.showOpenDialog(button.getScene().getWindow());

            //If a file was indeed chosen.
            if (file != null) {
                //Change the text of the button.
                button.setText("File: " + file.getName());

                //Read the table in the background.
                TableReadTask tableReadTask = new TableReadTask(this, file);
                Thread thread = new Thread(tableReadTask);
                thread.setDaemon(true);
                thread.start();
            }
        });
    }

    //Handle click for selection mode button.
    private void setChangeModeOnActionListener(SelectionModeButton button) {
        button.setOnMouseClicked(event -> {

            //If the table hasn't been selected yet----
            if (timetableSheet == null) {
                showInfoAlert("File not chosen yet!",
                        "Please choose a file first!");
                //------------------------

                //When a button other than the course button is clicked before the course button.
            } else if (button.getMode() != com.yli.timetable_assistant.example_selection.SelectionMode.SELECT_COURSE &&
                    !selectionModeToDataMap.containsKey(SelectionMode.SELECT_COURSE)) {
                showInfoAlert("Course not chosen yet!",
                        "Please choose a course first!");

                //When a button is clicked under proper conditions.
            } else {
                currentSelectionModeButton = button;
                instructionLabel.setText(currentSelectionModeButton.getInstruction());
            }
        });

    }


    /**
     * Initializes a GridPane to show a small part from the timetable, in which
     * the user can select a course and it's corresponding data as an example
     * to the program.
     *
     * @param sheet   Sheet to open window from.
     * @param rows    Number of rows in the window.
     * @param columns Number of columns in the window.
     */
    private void initTableSample(Sheet sheet, int rows, int columns) {
        //Clear the grid first since it might have been already populated with other children.
        tableSample.getChildren().clear();

        //Populate and set listener.
        for (int i = 0; i < rows; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < columns; j++) {
                Cell cell = row.getCell(j);
                String data = cell == null ? "" : cell.toString();
                Label label = new Label(data);
                GridPane.setFillWidth(label, true);
                GridPane.setFillHeight(label, true);
                label.getStyleClass().add("tableSampleLabel");
                tableSample.add(label, j, i);
                setOnTableSampleLabelClickListener(label,instructionLabel);
            }

        }
    }


    private void setOnTableSampleLabelClickListener(Label tableSampleLabel, Label instructionLabel) {
        tableSampleLabel.setOnMouseClicked(event -> {

            //A mode has to be selected or else we don't respond to any click.
            if (currentSelectionModeButton != null) {
                SelectionMode currentMode = currentSelectionModeButton.getMode();
                //Get the row and column of the label that was clicked.
                int rowIndex = GridPane.getRowIndex(tableSampleLabel);
                int columnIndex = GridPane.getColumnIndex(tableSampleLabel);

                //If the currently selected mode is for course cell selection.
                if (currentMode == SelectionMode.SELECT_COURSE) {
                    selectionModeToDataMap.putCourseCellData(columnIndex, rowIndex);
                    //If it's for course info selection.
                } else {
                    try {
                        selectionModeToDataMap.putCourseInfoCellData(
                                columnIndex, rowIndex, currentMode
                        );
                    } catch (ExampleCourseNotSetException | IncorrectExampleInfoException e) {
                        /*ExampleCourseNotSetException is unlikely to be thrown, since we are
                        prevent the user from even changing to other modes if the course has not
                        been selected yet,Thus the following dialog will assume that the user
                        has selected incorrect information.
                        */
                        showInfoAlert(bundle.getString("incorrectInfoHeader"), bundle.getString("incorrectInfoBody"));
                        return;
                    }
                }
                giveSelectionFeedback(currentSelectionModeButton,tableSampleLabel,instructionLabel);

            }


        });

    }

    /*Changes the text of the instructionLabel and the clicked button
    , and if all required info has been selected sets isReadyToSearch
     to true*/
    private void giveSelectionFeedback(SelectionModeButton button,Label tableSampleLabel,Label instructionLabel) {
        String instruction;

        if (selectionModeToDataMap.size() < com.yli.timetable_assistant.example_selection.SelectionMode.values().length) {
            instruction = bundle.getString("chooseRemainingInfo");
        } else {
            instruction = bundle.getString("allDone") ;
            isReadyToSearch = true;
        }

        instructionLabel.setText(instruction);
        button.setText(button.getCurrentlySelectedPrefix(tableSampleLabel.getText()));
        currentSelectionModeButton = null;
    }

    //Initializes controls that are related to adding courses.
    private void populateControlGrid() {

        //-----------------------------------------------------------
        /*Label and list for showing courses that have been added from
        the list that contains available courses*/
        Label addedCoursesHeader = new Label(bundle.getString("addedCoursesHeader"));
        addedCoursesHeader.getStyleClass().add("Header");

        ListView<String> addedCourses = new ListView<>();
        ObservableList<String> addedCoursesList = FXCollections.observableArrayList();
        addedCourses.setItems(addedCoursesList);

        setRemoveItemOnClickListener(addedCourses);
        //--------------------------------------------------------


        //----------------------------------------------------------
        //Label and list for displaying courses that are available in the sheet.
        Label availableCoursesHeader = new Label(bundle.getString("availableCoursesHeader"));
        availableCoursesHeader.getStyleClass().add("Header");
        ListView<String> availableCourses = new ListView<>();
        ObservableList<String> searchResultList = FXCollections.observableArrayList();
        availableCourses.setItems(
                searchResultList);

        setAddItemOnClickListener(availableCourses,addedCoursesList);
        //------------------------------------------------------------


        //------------------------------------------------------------
        //Controls for searching
        TextField searchField = new TextField();
        searchField.setPromptText(bundle.getString("searchFieldPrompt"));
        Button searchButton = new Button(bundle.getString("searchButton"));
        setSearchOnClickListener(searchButton,searchResultList,searchField);
        //--------------------------------------------------


        //-----------------------------------------------------------
        //generate table button
        Button generateButton = new Button(bundle.getString("generateButton"));

        setGenerateOnClickListener(generateButton,addedCoursesList);
        //------------------------------------------------------------


        //settings Box-------------------------------------
        Label settingsHeader = new Label(bundle.getString("settingsHeader"));
        settingsHeader.getStyleClass().add("Header");

        VBox settingsBox =  makeSettingsBox();
        //---------------------------------------------

        //Adding controls to grid
        controlGrid.add(searchField, 0, 0);
        controlGrid.add(searchButton, 1, 0);
        controlGrid.add(availableCoursesHeader, 0, 1);
        controlGrid.add(addedCoursesHeader, 1, 1);
        controlGrid.add(availableCourses, 0, 2);
        controlGrid.add(addedCourses, 1, 2);
        controlGrid.add(generateButton, 0, 3, 3, 1);
        controlGrid.add(settingsHeader, 2, 1);
        controlGrid.add(settingsBox, 2, 2);

    }


    //Returns a vbox containing some pref controls...
    private VBox makeSettingsBox(){
        VBox settingsBox = new VBox();
        settingsBox.getStyleClass().add("settingsBox");
        GridPane.setHgrow(settingsBox, Priority.ALWAYS);

        //example window size label and choice boxes
        Label exampleWindowSizeLabel = new Label(bundle.getString("exampleWindowSize"));
        HBox rowXColumn = new HBox();
        ObservableList<Integer> rowCol = FXCollections.observableArrayList();
        for (int i = 1; i <= 100; i++) rowCol.add(i);
        windowRowsChoiceBox.setItems(rowCol);
        windowColumnsChoiceBox.setItems(rowCol);
        windowRowsChoiceBox.setValue(5);
        windowColumnsChoiceBox.setValue(5);
        rowXColumn.getChildren().addAll(windowRowsChoiceBox, new Label("*"), windowColumnsChoiceBox);

        settingsBox.getChildren().addAll(
                exampleWindowSizeLabel,
                rowXColumn
        );
        return settingsBox;
    }

    //Handle clicking generate..
    private void setGenerateOnClickListener(Button generateButton,ObservableList<String> generateFrom){
        IntsBundle intBundle = (IntsBundle)ResourceBundle.getBundle(IntsBundle.class.getCanonicalName());
        generateButton.setOnAction(event -> {
            if (!generateFrom.isEmpty()) {
                DayToCourseListMap map = TableUtils.makeDayToCourseListMap(timetableSheet, selectionModeToDataMap, generateFrom);
                FXUtils.openWindow(bundle.getString("yourTimetable"), new Stage(),
                        intBundle.getInteger("windowWidth"), intBundle.getInteger("windowHeight"),
                        GeneratedTableController.class.getResource(GeneratedTableController.FXML_PATH),
                        ResourceBundle.getBundle(StringsBundle.class.getCanonicalName()),
                        new GeneratedTableController(map));
            } else
                showInfoAlert(bundle.getString("notReadyToGenerateHeader"), bundle.getString("notReadyToGenerateBody"));
        });
    }

    //Handle clicking search...
    private void setSearchOnClickListener(Button searchButton,ObservableList<String> searchResultList,TextField searchField){
        searchButton.setOnAction(event -> {
            if (isReadyToSearch) {
                TableUtils.search(timetableSheet, searchResultList, searchField.getText());

            } else {
                showInfoAlert(bundle.getString("insufficientInfoHeader"), bundle.getString("insufficientInfoBody"));
            }
        });
    }

    //Handle removing an item from a list view.
    private void setRemoveItemOnClickListener(ListView<String> removingFrom){
        removingFrom.setOnMouseClicked(event -> {
            MultipleSelectionModel<String> sModel = removingFrom.getSelectionModel();
            String string = sModel.getSelectedItem();
            if (string != null)
                removingFrom.getItems()
                        .remove(sModel.getSelectedIndex());
        });
    }

    //Handle adding an item from a list view to a list of another.
    private void setAddItemOnClickListener(ListView<String> addingFrom, ObservableList<String> addingTo){
        addingFrom.setOnMouseClicked(event -> {
            String clickedItem = addingFrom.getSelectionModel().getSelectedItem();
            if (!addingTo.contains(clickedItem) && clickedItem != null) {
                addingTo.add(clickedItem
                );
            }
        });

    }


    @Override
    public void onLoading() {
        browseButton.setDisable(true);
        tableSample.setVisible(false);
        progressIndicator.setVisible(true);

    }

    @Override
    public void onFinishedLoading(Workbook timetable) {
        if (timetable != null) {
            timetableSheet = timetable.getSheetAt(0);
            TableUtils.unpackMergedCells(timetableSheet);
            initTableSample(timetableSheet, windowRowsChoiceBox.getValue(), windowColumnsChoiceBox.getValue());
        }
        progressIndicator.setVisible(false);
        tableSample.setVisible(true);
        browseButton.setDisable(false);

    }


}
