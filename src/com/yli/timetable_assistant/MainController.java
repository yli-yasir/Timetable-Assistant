package com.yli.timetable_assistant;

import com.yli.timetable_assistant.choosefile.ChooseFileButton;
import com.yli.timetable_assistant.choosefile.LoadFromComputerMenuItem;
import com.yli.timetable_assistant.choosefile.LoadFromURLMenuItem;
import com.yli.timetable_assistant.exampleselection.RowsColsComboBox;
import com.yli.timetable_assistant.exampleselection.*;
import com.yli.timetable_assistant.exampleselection.SelectionMode;
import com.yli.timetable_assistant.table.GridCell;
import com.yli.timetable_assistant.utils.FXUtils;
import com.yli.timetable_assistant.res.Integers;
import com.yli.timetable_assistant.res.StringsBundle;
import com.yli.timetable_assistant.table.DayToCourseListMap;
import com.yli.timetable_assistant.table.TableUtils;
import com.yli.timetable_assistant.tasks.CallbackTask;
import com.yli.timetable_assistant.tasks.TableReadTask;
import com.yli.timetable_assistant.utils.IOUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.util.ResourceBundle;

import static com.yli.timetable_assistant.utils.FXUtils.showErrorAlert;

class MainController {

    //path for fxml resource for this controller.
    static final String FXML_PATH = "/com/yli/timetable_assistant/res/main.fxml";
    //The sheet that contains the timetable.
    private Sheet timetableSheet;
    //A label to guide the user through the example selection process.
    @FXML
    private Label instructionLabel;
    //Contains the combo boxes that control the table sample size.
    @FXML
    private HBox tableSampleSizeControlsContainer;
    private RowsColsComboBox tableSampleColumnsComboBox = new RowsColsComboBox();
    private RowsColsComboBox tableSampleRowsComboBox = new RowsColsComboBox();
    //Contains choosefile that have to do with selecting example course info.
    @FXML
    private HBox exampleSelectionControlsContainer;
    private ChooseFileButton chooseFileButton;
    //This contains the mode buttons.
    private ModeButtonToggleGroup modeToggleGroup;
    /*A grid which will be populated with labels which represent cells from the sheet
    the user will use this to set an example for the program*/
    @FXML
    private GridPane tableSample;
    //Progress indicator which will be shown or hidden at loading.
    @FXML
    private ProgressIndicator progressIndicator;
    /*A grid which contains all choosefile other than the ones
     that have to do with example selection.*/
    @FXML
    private GridPane courseOperationsGrid;
    //This will hold the data selected in each mode.
    private static SelectionModeToDataMap selectionModeToDataMap = new SelectionModeToDataMap();
    //search query field.
    private TextField searchField;
    //This will contain the search results of the courses.
    private ObservableList<String> searchResultList;
    //This will contain the courses that user wants to be included in his table.
    private ObservableList<String> addedCoursesList;
    @FXML
    private GridPane generatedTableGrid;

    //Bundle which has string resources that will be used in the GUI.
    private ResourceBundle strings = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());

    /*This will be automatically called after injecting the variables above
    with their values*/
    @FXML
    private void initialize() {
        initChooseFileButton();
        populateTableSampleSizeControlsContainer();
        populateExampleSelectionControlBar();
        populateCourseOperationsGrid();
    }

    //the choose file button will also be added here!
    //put the combo boxes and label inside their container.
    private void populateTableSampleSizeControlsContainer() {

        //make labels for the combo boxes
        Label rowsLabel = new Label(strings.getString("rows") + ": ");
        Label columnsLabel = new Label(strings.getString("columns") + ": ");

        tableSampleSizeControlsContainer.getChildren().addAll(
                chooseFileButton,
                rowsLabel, tableSampleRowsComboBox,
                columnsLabel, tableSampleColumnsComboBox);
    }

    //populate the combo boxes with numbers
    private void populateColsRowsComboBoxes() {
        if (timetableSheet != null) {
            //When the combo boxes change repopulate the table...
            EventHandler<ActionEvent> tableSampleComboBoxChange = e -> {
                if (timetableSheet != null) {
                    TableUtils.populateTableSampleGrid(
                            tableSample,
                            timetableSheet,
                            tableSampleRowsComboBox.getValue()
                            ,tableSampleColumnsComboBox.getValue(),
                            ev->{handleSampleTableCellClick((GridCell)ev.getSource());});
                }
            };

            tableSampleRowsComboBox.populate(TableUtils.getTableRowCount(timetableSheet),
                    Integers.TABLE_SAMPLE_DEFAULT_HEIGHT,
                    tableSampleComboBoxChange);

            tableSampleColumnsComboBox.populate(TableUtils.getTableColCount(timetableSheet),
                    Integers.TABLE_SAMPLE_DEFAULT_WIDTH,
                    tableSampleComboBoxChange);
        }

    }

    /*populates with choosefile which will be used to choose example data
  from the sample table*/
    private void populateExampleSelectionControlBar() {

        initToggleGroup();

        ObservableList<Node> children = exampleSelectionControlsContainer.getChildren();

        //Add the selection mode buttons
        modeToggleGroup.getToggles().forEach(toggle -> children.add((Node) toggle));

        children.forEach(child -> {
            //the following will make all the buttons take the same size.
            HBox.setHgrow(child, Priority.ALWAYS);
            //lock it as max size to prevent from overgrowth.
            //todo((Control) child).setMaxWidth(/ children.size())
        });


    }


    private void initChooseFileButton() {
        chooseFileButton = new ChooseFileButton(strings);

        LoadFromURLMenuItem loadFromURL = chooseFileButton.getOwnMenu().getLoadFromURLMenuItem();

        loadFromURL.setOnReceiveResult(url -> {

            chooseFileButton.setText(strings.getString("file") + ": "
                    + strings.getString("loadFromInternet"));

            IOUtils.loadFromURL(url, IOUtils.makeTempFile(strings),new FetchOnlineFileCallbacks());
        });

        LoadFromComputerMenuItem loadFromComputer = chooseFileButton.getOwnMenu().getLoadFromComputerMenuItem();
        loadFromComputer.setOnReceiveResult(file -> {

            chooseFileButton.setText(strings.getString("file") + ": " +
                    strings.getString("loadFromPC"));

            IOUtils.loadFromFile(file,new ReadTableCallbacks());

        });

    }

    private void initToggleGroup() {

        modeToggleGroup = new ModeButtonToggleGroup(e->
                handleModeButtonClick((ModeButton)e.getSource()));

    }

    private void handleModeButtonClick(ModeButton button) {

        //Reject changes if file isn't selected yet.
        if (!isFileLoaded()) {
            button.setSelected(false);
            showErrorAlert(strings, "fileNotChosenHeader",
                    "fileNotChosenBody");
        }

        //Reject changes when a button other than the course button is clicked and the course hasn't been selected.
        else if (button.getMode() != SelectionMode.SELECT_COURSE && !selectionModeToDataMap.isCourseSelected()) {
            button.setSelected(false);
            showErrorAlert(strings, "courseNotChosenHeader",
                    "courseNotChosenBody");
            //select the course toggle for the user
            ModeButton courseButton = (ModeButton) modeToggleGroup.getToggles().get(0);
            courseButton.setSelected(true);
        }

        //the button may be being selected or unselected so we have to check first.
        else {
            if (button.isSelected()) {
                instructionLabel.setText(
                        modeToggleGroup.getToggledModeButton()
                                .getInstruction()
                );
            }
            //If its being unselected then prompt the user to select a button
            else {
                instructionLabel.setText(strings.getString("chooseRemainingInfo"));
            }
        }
    }

    private boolean isFileLoaded() {
        return timetableSheet != null;
    }


    //Resets all the mode buttons in a container.
    private void resetModeButtons(boolean includeToggledButton) {
        if (includeToggledButton) {
            modeToggleGroup.selectToggle(null);
        }

        modeToggleGroup.getToggles().forEach(toggle -> {
            ModeButton button = ((ModeButton) toggle);
            button.setText(button.getOriginalLabel());
        });


    }

    //Clears selected example info.
    private void clearExampleSelection(boolean includeToggledButton) {

        /*Clear the map. If other course information was already selected,
         that information was built in reference to this certain course.
         Which you might be changing now. Thus the old information is now incorrect*/
        selectionModeToDataMap.clear();

        //reset mode buttons in the exampleSelectionControlsContainer.
        resetModeButtons(includeToggledButton);

    }

    //Clears operations done by the user related to courses such as search query text, results, and added courses.
    private void clearUserCourseOperations() {
        searchField.clear();
        searchResultList.clear();
        addedCoursesList.clear();
    }

    //Clears outdated form info.
    private void clearForm(boolean includeToggledButton) {
        clearExampleSelection(includeToggledButton);
        clearUserCourseOperations();
    }

    private void resetForm(boolean includeToggledButton) {
        instructionLabel.setText(strings.getString("chooseRemainingInfo"));
        clearForm(includeToggledButton);
    }


    private void handlePutDataSuccess(ModeButton button, Label label) {

        giveSelectionFeedback(button, label, instructionLabel);

        //if this is the last button
        if (modeToggleGroup.isLastButtonToggled()) {
            button.setSelected(false);
        }
        //Select the next button
        else {
            ModeButton nextButton = modeToggleGroup.getNextButton();
            //if the button after it hasn't been used
            if (!selectionModeToDataMap.containsKey(nextButton.getMode())) {
                //simulate a user click on the next button
                nextButton.setSelected(true);
                handleModeButtonClick(nextButton);
            } else {
                button.setSelected(false);
            }
        }
    }

    private boolean putSelectedData(Label tableSampleLabel, SelectionMode currentMode) {
        //Get the row and column of the label that was clicked.
        int rowIndex = GridPane.getRowIndex(tableSampleLabel);
        int columnIndex = GridPane.getColumnIndex(tableSampleLabel);

        //If the currently selected mode is for course cell selection.
        if (currentMode == SelectionMode.SELECT_COURSE) {
            /* If other course information was already selected,
             that information was built in reference to this certain course.
              Which you might be changing now. Thus the old information is now incorrect,
             */
            //do not include the currently toggled button as it's needed
            //as it's text needs to be altered.
            resetForm(false);

            selectionModeToDataMap.putCourseCellData(columnIndex, rowIndex);

            return true;

            //If it's for course info cell selection.
        } else {
            try {
                selectionModeToDataMap.putCourseInfoCellData(
                        columnIndex, rowIndex, currentMode
                );
                return true;
            } catch (ExampleCourseNotSetException | IncorrectExampleInfoException e) {
                        /*ExampleCourseNotSetException is unlikely to be thrown, since we are
                        prevent the user from even changing to other modes if the course has not
                        been selected yet,Thus the following dialog will assume that the user
                        has selected incorrect information.
                        */
                showErrorAlert(strings, "incorrectInfoHeader", "incorrectInfoBody");
                return false;
            }
        }
    }

    private void handleSampleTableCellClick(GridCell cell){

            //ignore any clicks if a button is not selected
            if (modeToggleGroup.isButtonToggled()) {
                ModeButton button = modeToggleGroup.getToggledModeButton();

                //this method will clear the currently selected button...
                //If data was successfully put
                if (putSelectedData(cell, button.getMode())) {
                    handlePutDataSuccess(button, cell);
                }
            }
    }

    /*Changes the text of the instructionLabel and the clicked button*/
    private void giveSelectionFeedback(ModeButton button, Label tableSampleLabel, Label instructionLabel) {
        String instruction = isReadyToSearch() ? strings.getString("allDone") : strings.getString("chooseRemainingInfo");
        instructionLabel.setText(instruction);
        button.setText(strings.getString(button.getMode().nameKey()) +
                ": " + tableSampleLabel.getText());
    }

    private boolean isReadyToSearch() {
        return !(selectionModeToDataMap.size() < modeToggleGroup.getToggles().size());
    }

    private Label makeHeaderLabel(String text) {
        Label headerLabel = new Label(text);
        headerLabel.getStyleClass().add("Header");
        return headerLabel;
    }

    //remove an item from a list view
    private void removeItem(ListView<String> removingFrom) {
        MultipleSelectionModel<String> sModel = removingFrom.getSelectionModel();
        String string = sModel.getSelectedItem();
        if (string != null)
            removingFrom.getItems()
                    .remove(sModel.getSelectedIndex());
    }

    //Handle adding an item from a list view to a list of another.
    private void addItem(ListView<String> addingFrom, ObservableList<String> addingTo) {
        String clickedItem = addingFrom.getSelectionModel().getSelectedItem();
        if (!addingTo.contains(clickedItem) && clickedItem != null) {
            addingTo.add(clickedItem
            );
        }
    }

    //Initializes choosefile that are related to manipulating courses.
    private void populateCourseOperationsGrid() {

        //-----------------------------------------------------------
        /*Label and list view for showing courses that have been added from
        the list that contains available courses*/
        Label addedCoursesHeader = makeHeaderLabel(strings.getString("addedCoursesHeader"));

        ListView<String> addedCourses = new ListView<>();
        addedCoursesList = FXCollections.observableArrayList();
        addedCourses.setItems(addedCoursesList);

        addedCourses.setOnMouseClicked(e -> {
            removeItem(addedCourses);
            generateTable(addedCoursesList);
        });
        //--------------------------------------------------------


        //----------------------------------------------------------
        //Label and list for displaying courses that are available in the sheet.
        Label availableCoursesHeader = makeHeaderLabel(strings.getString("availableCoursesHeader"));

        ListView<String> availableCourses = new ListView<>();
        searchResultList = FXCollections.observableArrayList();
        availableCourses.setItems(searchResultList);

        availableCourses.setOnMouseClicked(e -> {
            addItem(availableCourses, addedCoursesList);
            generateTable(addedCoursesList);
        });
        //------------------------------------------------------------


        //------------------------------------------------------------
        //Controls for searching
        searchField = new TextField();
        searchField.setPromptText(strings.getString("searchFieldPrompt"));

        searchField.setOnKeyReleased(event -> {
            if (isReadyToSearch()) {
                TableUtils.search(timetableSheet, searchResultList, searchField.getText());
            } else {
                showErrorAlert(strings, "insufficientInfoHeader", "insufficientInfoBody");
                searchField.clear();
            }
        });
        //--------------------------------------------------


        //-----------------------------------------------------------

        //------------------------------------------------------------

        //Add choosefile to grid
        courseOperationsGrid.add(searchField, 0, 0);
        courseOperationsGrid.add(availableCoursesHeader, 0, 1);
        courseOperationsGrid.add(addedCoursesHeader, 1, 1);
        courseOperationsGrid.add(availableCourses, 0, 2);
        courseOperationsGrid.add(addedCourses, 1, 2);

    }

    private void generateTable(ObservableList<String> generateFrom) {

        DayToCourseListMap map = TableUtils.makeDayToCourseListMap(timetableSheet, selectionModeToDataMap, generateFrom);
        TableUtils.populateGeneratedTableGrid(generatedTableGrid, map);

    }

    private void taskLoadingMode() {
        chooseFileButton.setDisable(true);
        tableSample.setVisible(false);
        progressIndicator.setVisible(true);
    }

    private void taskFailedMode() {
        chooseFileButton.setDisable(false);
        progressIndicator.setVisible(false);
        tableSample.setVisible(true);
        tableSample.getChildren().clear();
        timetableSheet = null;
        chooseFileButton.setText(strings.getString("chooseFileButton"));
    }

    private class ReadTableCallbacks implements CallbackTask.TaskCallbacks<Workbook> {
        @Override
        public void onLoading() {
            taskLoadingMode();
        }

        @Override
        public void onSucceeded(Workbook timetable) {
            resetForm(true);
            if (timetable != null) {
                timetableSheet = timetable.getSheetAt(0);
                TableUtils.unpackMergedCells(timetableSheet);
                //populate the combo boxes lists will be used as source for combo boxes
                //When they are populated they will trigger table load
                populateColsRowsComboBoxes();

                TableUtils.populateTableSampleGrid(
                        tableSample,
                        timetableSheet,
                        Integers.TABLE_SAMPLE_DEFAULT_HEIGHT,
                        Integers.TABLE_SAMPLE_DEFAULT_WIDTH,
                        ev-> handleSampleTableCellClick((GridCell)ev.getSource())
                        );


                //simulate a click on the first button, in the toggle group
                ModeButton firstButton = (ModeButton) modeToggleGroup.getToggles().get(0);
                firstButton.setSelected(true);
                handleModeButtonClick(firstButton);

            }
            progressIndicator.setVisible(false);
            tableSample.setVisible(true);
            chooseFileButton.setDisable(false);

        }

        @Override
        public void onFailed(Throwable e) {
            FXUtils.showErrorAlert(strings, "badIOHeader", "badIOBody");
            taskFailedMode();
        }

    }

    private class FetchOnlineFileCallbacks implements CallbackTask.TaskCallbacks<File> {

        @Override
        public void onLoading() {
            taskLoadingMode();
        }

        @Override
        public void onSucceeded(File result) {
            TableReadTask readTask = new TableReadTask(new ReadTableCallbacks(), result);
            IOUtils.startTask(readTask);
        }

        @Override
        public void onFailed(Throwable e) {
            FXUtils.showErrorAlert(strings, "badNetworkIOHeader", "badNetworkIOBody");
            taskFailedMode();

        }
    }


}
