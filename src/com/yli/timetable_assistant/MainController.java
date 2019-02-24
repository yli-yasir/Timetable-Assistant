package com.yli.timetable_assistant;

import com.yli.timetable_assistant.controls.ChooseFileButton;
import com.yli.timetable_assistant.controls.LoadFromComputerMenuItem;
import com.yli.timetable_assistant.controls.LoadFromURLMenuItem;
import com.yli.timetable_assistant.example_selection.IncorrectExampleInfoException;
import com.yli.timetable_assistant.fx.FXUtils;
import com.yli.timetable_assistant.res.Integers;
import com.yli.timetable_assistant.res.StringsBundle;
import com.yli.timetable_assistant.table.DayToCourseListMap;
import com.yli.timetable_assistant.table.TableUtils;
import com.yli.timetable_assistant.tasks.CallbackTask;
import com.yli.timetable_assistant.tasks.FetchOnlineFileTask;
import com.yli.timetable_assistant.tasks.TableReadTask;
import com.yli.timetable_assistant.example_selection.ExampleCourseNotSetException;
import com.yli.timetable_assistant.example_selection.SelectionMode;
import com.yli.timetable_assistant.example_selection.ModeButton;
import com.yli.timetable_assistant.example_selection.SelectionModeToDataMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import static com.yli.timetable_assistant.fx.FXUtils.showErrorAlert;

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
    private ComboBox<Integer> tableSampleColumnsComboBox = new ComboBox<>();
    private ComboBox<Integer> tableSampleRowsComboBox = new ComboBox<>();
    //Contains controls that have to do with selecting example course info.
    @FXML
    private HBox exampleSelectionControlsContainer;
    private ChooseFileButton chooseFileButton;
    //This contains the mode buttons.
    private ToggleGroup modeToggleGroup = new ToggleGroup();
    /*A grid which will be populated with labels which represent cells from the sheet
    the user will use this to set an example for the program*/
    @FXML
    private GridPane tableSample;
    //Progress indicator which will be shown or hidden at loading.
    @FXML
    private ProgressIndicator progressIndicator;
    /*A grid which contains all controls other than the ones
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
        populateTableSampleSizeControlsContainer();
        populateExampleSelectionControlBar();
        populateCourseOperationsGrid();
    }

    //put the combo boxes and label inside their container.
    private void populateTableSampleSizeControlsContainer() {
        //make labels for the combo boxes
        Label rowsLabel = new Label(strings.getString("rows") + ": ");
        Label columnsLabel = new Label(strings.getString("columns") + ": ");

        tableSampleSizeControlsContainer.getChildren().addAll(
                rowsLabel, tableSampleRowsComboBox,
                columnsLabel, tableSampleColumnsComboBox);
    }

    //populate the combo boxes with numbers
    private void populateColsRowsComboBoxes() {
        if (timetableSheet != null) {

            //remove event handlers so they don't trigger reloads when we are populating them.
            tableSampleColumnsComboBox.setOnAction(null);
            tableSampleRowsComboBox.setOnAction(null);

            ObservableList<Integer> cols = tableSampleColumnsComboBox.getItems();
            ObservableList<Integer> rows = tableSampleRowsComboBox.getItems();

            //clear the lists since they might have been already populated.
            cols.clear();
            rows.clear();

            //populate the rows list with numbers
            for (int i = 1; i <= TableUtils.getTableRowCount(timetableSheet); i++)
                rows.add(i);
            //populate the columns list with numbers
            for (int i = 1; i <= TableUtils.getTableColCount(timetableSheet); i++)
                cols.add(i);

            tableSampleColumnsComboBox.setValue(Integers.TABLE_SAMPLE_DEFAULT_WIDTH);
            tableSampleRowsComboBox.setValue(Integers.TABLE_SAMPLE_DEFAULT_HEIGHT);


            EventHandler<ActionEvent> tableSampleComboBoxChange = e -> {
                if (timetableSheet != null) {
                    populateTableSample(timetableSheet,
                            tableSampleRowsComboBox.getValue()
                            , tableSampleColumnsComboBox.getValue());
                }
            };

            //add back the event handlers
            tableSampleColumnsComboBox.setOnAction(tableSampleComboBoxChange);
            tableSampleRowsComboBox.setOnAction(tableSampleComboBoxChange);
        }
    }

    /*populates with controls which will be used to choose example data
  from the sample table*/
    private void populateExampleSelectionControlBar() {
        initChooseFileButton();
        initToggleGroup();

        ObservableList<Node> children = exampleSelectionControlsContainer.getChildren();

        //Add the choose file control first.
        children.add(chooseFileButton);

        //Add the selection mode buttons.
        modeToggleGroup.getToggles().forEach(toggle -> children.add((Node) toggle));

        children.forEach(child -> {
            //the following will make all the buttons take the same size.
            HBox.setHgrow(child, Priority.ALWAYS);
            //lock it as max size to prevent from overgrowth.
            ((Control) child).setMaxWidth(Integers.WINDOW_WIDTH / children.size())
            ;
        });


    }

    private void initChooseFileButton() {
        chooseFileButton = new ChooseFileButton(strings);

        LoadFromURLMenuItem loadFromURL = chooseFileButton.getOwnMenu().getLoadFromURLMenuItem();

        loadFromURL.setOnReceiveResult(url -> {

            chooseFileButton.setText(strings.getString("file") + ": "
                    + strings.getString("loadFromInternet"));

            loadFromURL(url, makeTempFile());
        });

        LoadFromComputerMenuItem loadFromComputer = chooseFileButton.getOwnMenu().getLoadFromComputerMenuItem();
        loadFromComputer.setOnReceiveResult(file -> {

            chooseFileButton.setText(strings.getString("file") + ": " +
                    strings.getString("loadFromPC"));

            loadFromFile(file);

        });

    }


    //Loads Url into the file, in a background thread with a task.
    private void loadFromURL(String url, File tmpFile) {
        if (tmpFile != null) {
            FetchOnlineFileTask fetchOnlineFileTask = new FetchOnlineFileTask(new FetchOnlineFileCallbacks(),
                    url, tmpFile);
            startTask(fetchOnlineFileTask);
        }
    }

    //Read the table in the background.
    private void loadFromFile(File file) {
        TableReadTask tableReadTask = new TableReadTask(new ReadTableCallbacks(), file);
        startTask(tableReadTask);
    }

    private void initToggleGroup() {

        modeToggleGroup = new ToggleGroup();

        //make as many buttons as needed for selection modes, in here I am making
        //a button for each mode.
        for (SelectionMode mode : SelectionMode.values()) {

            ModeButton button = new ModeButton(mode);

            button.setOnAction(e ->
                    handleModeButtonClick(button));

            button.setToggleGroup(modeToggleGroup);
        }
    }

    private void handleModeButtonClick(ModeButton button) {

        //Reject changes if file isn't selected yet.
        if (!isFileLoaded()) {
            button.setSelected(false);
            showErrorAlert(strings, "fileNotChosenHeader",
                    "fileNotChosenBody");
        }

        //Reject changes when a button other than the course button is clicked and the course hasn't been selected.
        else if (button.getMode() != SelectionMode.SELECT_COURSE && !isCourseSelected()) {
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
                        getToggledModeButton()
                                .getInstruction()
                );
            }
            //If its being unselected then prompt the user to select a button
            else {
                instructionLabel.setText(strings.getString("chooseRemainingInfo"));
            }
        }
    }


    private ModeButton getToggledModeButton() {
        return ((ModeButton) modeToggleGroup.getSelectedToggle());
    }

    //Is there any button that's toggled?
    private boolean isButtonToggled() {
        return modeToggleGroup.getSelectedToggle() != null;
    }

    private boolean isFileLoaded() {
        return timetableSheet != null;
    }

    private boolean isCourseSelected() {
        return selectionModeToDataMap.containsKey(SelectionMode.SELECT_COURSE);
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

    private File makeTempFile() {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("TA_TMP", null);
            tmpFile.deleteOnExit();
        } catch (IOException io) {
            FXUtils.showErrorAlert(strings, "badTempFileIOHeader", "badTempFileIOBody");
        }
        return tmpFile;
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
    private void populateTableSample(Sheet sheet, int rows, int columns) {
        System.out.println("populating" + rows + " " + columns);

        //Clear the grid first since it might have been already populated with other children.
        tableSample.getChildren().clear();

        //For each row in the table
        for (int currentRowIndex = 0; currentRowIndex < rows; currentRowIndex++) {
            Row row = sheet.getRow(currentRowIndex);
            //For each column in that row
            for (int currentColumnIndex = 0; currentColumnIndex < columns; currentColumnIndex++) {
                //Get the cell at that column
                Cell cell = row.getCell(currentColumnIndex);
                //Get the text from the cell
                String text = TableUtils.makeStringValue(cell, false);
                //make a table sample label from the text
                //Each label in the grid, represents a cell in the table sample
                Label tableSampleLabel = makeTableSampleLabel(text);
                tableSample.add(tableSampleLabel, currentColumnIndex, currentRowIndex);

            }

        }
    }

    //Each label in the grid, represents a cell in the table sample
    private Label makeTableSampleLabel(String text) {
        Label label = new Label(text);
        GridPane.setFillWidth(label, true);
        GridPane.setFillHeight(label, true);
        label.getStyleClass().add("tableSampleLabel");
        label.setTooltip(new Tooltip(text));

        //set click listener
        label.setOnMouseClicked(e -> {

            //ignore any clicks if a button is not selected
            if (isButtonToggled()) {
                ModeButton button = getToggledModeButton();


                //this method will clear the currently selected button...
                //If data was successfully put
                if (putSelectedData(label, button.getMode())) {
                    handlePutDataSuccess(button, label);
                }


            }
        });


        return label;
    }

    private void handlePutDataSuccess(ModeButton button, Label label) {

        giveSelectionFeedback(button, label, instructionLabel);

        int currentButtonIndex = modeToggleGroup.getToggles().indexOf(
                modeToggleGroup.getSelectedToggle());

        //if this is the last button
        if (currentButtonIndex + 1 == modeToggleGroup.getToggles().size()) {
            button.setSelected(false);
        }
        //Select the next button
        else {
            ModeButton nextButton = (ModeButton) modeToggleGroup.getToggles().get(currentButtonIndex + 1);
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

    //Initializes controls that are related to manipulating courses.
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

        //Add controls to grid
        courseOperationsGrid.add(searchField, 0, 0);
        courseOperationsGrid.add(availableCoursesHeader, 0, 1);
        courseOperationsGrid.add(addedCoursesHeader, 1, 1);
        courseOperationsGrid.add(availableCourses, 0, 2);
        courseOperationsGrid.add(addedCourses, 1, 2);

    }

    //todo Currently this is a stub. Might add option to change course details in this box.
    private VBox makeExtrasBox() {
        VBox extraBox = new VBox();
        extraBox.getStyleClass().add("extrasBox");
        GridPane.setHgrow(extraBox, Priority.ALWAYS);
        return extraBox;
    }

    private void generateTable(ObservableList<String> generateFrom) {

        DayToCourseListMap map = TableUtils.makeDayToCourseListMap(timetableSheet, selectionModeToDataMap, generateFrom);
        TableUtils.populateGrid(generatedTableGrid, map);

    }

    private void startTask(Task task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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

                populateTableSample(timetableSheet,
                        Integers.TABLE_SAMPLE_DEFAULT_HEIGHT,
                        Integers.TABLE_SAMPLE_DEFAULT_WIDTH
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
            startTask(readTask);
        }

        @Override
        public void onFailed(Throwable e) {
            FXUtils.showErrorAlert(strings, "badNetworkIOHeader", "badNetworkIOBody");
            taskFailedMode();

        }
    }


}
