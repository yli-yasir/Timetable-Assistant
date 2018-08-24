package com.yli.timetable_assistant;

import com.yli.timetable_assistant.example_selection.IncorrectExampleInfoException;
import com.yli.timetable_assistant.fx.FXUtils;
import com.yli.timetable_assistant.res.IntsBundle;
import com.yli.timetable_assistant.res.StringsBundle;
import com.yli.timetable_assistant.table.DayToCourseListMap;
import com.yli.timetable_assistant.table.TableUtils;
import com.yli.timetable_assistant.tasks.CallbackTask;
import com.yli.timetable_assistant.tasks.FetchOnlineFileTask;
import com.yli.timetable_assistant.tasks.TableReadTask;
import com.yli.timetable_assistant.example_selection.ExampleCourseNotSetException;
import com.yli.timetable_assistant.example_selection.SelectionMode;
import com.yli.timetable_assistant.buttons.SelectionModeButton;
import com.yli.timetable_assistant.example_selection.SelectionModeToDataMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import static com.yli.timetable_assistant.fx.FXUtils.showAlert;

class MainController {

    //Relative path for the FXML for this controller.
    static final String FXML_PATH = "/com/yli/timetable_assistant/res/main.fxml";

    //The sheet that contains the timetable.
    private Sheet timetableSheet;

    //A label to guide the user through the example selection process.
    @FXML
    private Label instructionLabel;

    private Button chooseFileButton;

    //Contains controls that have to do with selecting example course info.
    @FXML
    private HBox exampleSelectionControlBar;

    //todo change name
    //The selection mode button that's currently selected.
    private SelectionModeButton currentSelectionModeButton;

    /*A grid which will be populated with labels which represent cells from the sheet*/
    @FXML
    private GridPane tableSample;

    /*A grid which contains all controls other than the ones
     that have to do with example selection.*/
    @FXML
    private GridPane courseOperationsGrid;

    //Progress indicator which will be shown or hidden at loading.
    @FXML
    private ProgressIndicator progressIndicator;

    //This will hold the data selected in each mode.
    private static SelectionModeToDataMap selectionModeToDataMap = new SelectionModeToDataMap();

    //search query field.
    private TextField searchField;

    //
    private ObservableList<String> searchResultList;

    private ObservableList<String> addedCoursesList;

    //Bundle which has string resources that will be used in the GUI.
    private ResourceBundle bundle = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());


    /*This will be automatically called after injecting the variables above
    with their values*/
    @FXML
    private void initialize() {
        populateExampleSelectionControlBar();
        populateCourseOperationsGrid();
    }

    /*populates with controls which will be used to choose example data
  from the sample table*/
    private void populateExampleSelectionControlBar() {
        //Get a ref to the children since we will be adding to them repeatedly.
        ObservableList<Node> children = exampleSelectionControlBar.getChildren();

        //Add the choose file control first.
        children.add(makeChooseFileButton());

        //Add the selection mode buttons.
        children.addAll(makeSelectionModeButtons());
    }

    private Button makeChooseFileButton() {
        chooseFileButton = new Button(bundle.getString("chooseFileButton"));

        HBox.setHgrow(chooseFileButton, Priority.ALWAYS);

        ContextMenu chooseFileContextMenu = buildChooseFileContextMenu();
        chooseFileButton.setOnAction(e -> chooseFileContextMenu.show(chooseFileButton, Side.BOTTOM, 0, 0));

        return chooseFileButton;
    }

    //Build a context menu to show.
    private ContextMenu buildChooseFileContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem loadFromURLMenuItem = new MenuItem(bundle.getString("loadFromInternet"));

        loadFromURLMenuItem.setOnAction(e -> {
            chooseFileButton.setText(bundle.getString("filePrefix") + " "
                    + bundle.getString("loadFromInternet"));

            loadFromURL("http://docs.neu.edu.tr/library/timetable.xlsx", makeTempFile());
        });

        MenuItem loadFromComputerMenuItem = new MenuItem(bundle.getString("loadFromPC"));

        loadFromComputerMenuItem.setOnAction(e -> {
            File file = browseFile(loadFromComputerMenuItem.getParentPopup().getOwnerWindow());
            //If a file was indeed chosen.
            if (file != null) {
                chooseFileButton.setText(bundle.getString("filePrefix") + " " +
                        bundle.getString("loadFromPC"));
                loadFromFile(file);
            }
        });

        menu.getItems().addAll(loadFromURLMenuItem, loadFromComputerMenuItem);

        return menu;
    }

    //Loads Url into the file, in a background thread with a task.
    private void loadFromURL(String url, File tmpFile) {
        if (tmpFile != null) {
            FetchOnlineFileTask fetchOnlineFileTask = new FetchOnlineFileTask(new FetchOnlineFileCallbacks(),
                    url, tmpFile);
            startTask(fetchOnlineFileTask);
        }
    }

    private void loadFromFile(File file) {
        //Read the table in the background.
        TableReadTask tableReadTask = new TableReadTask(new ReadTableCallbacks(), file);
        startTask(tableReadTask);
    }

    private Collection<SelectionModeButton> makeSelectionModeButtons() {

        ArrayList<SelectionModeButton> selectionModeButtons = new ArrayList<>();

        //make as many buttons as needed for selection modes, in here I am making
        //a button for each mode.
        for (SelectionMode mode : SelectionMode.values()) {

            SelectionModeButton button = new SelectionModeButton(mode);

            HBox.setHgrow(button, Priority.ALWAYS);

            button.setOnAction(e -> {
                changeCurrentlySelectedModeButton(button);
                if (isModeButtonSelected()) instructionLabel.setText(currentSelectionModeButton.getInstruction());
            });

            selectionModeButtons.add(button);
        }
        return selectionModeButtons;
    }

    private void changeCurrentlySelectedModeButton(SelectionModeButton button) {

        //Reject changes if file isn't selected yet.
        if (!isFileLoaded()) {
            showAlert(bundle, "fileNotChosenHeader",
                    "fileNotChosenBody");
        }

        //Reject changes when a button other than the course button is clicked and the course hasn't been selected.
        else if (button.getMode() != SelectionMode.SELECT_COURSE && !isCourseSelected()) {
            showAlert(bundle, "courseNotChosenHeader",
                    "courseNotChosenBody");
        } else {
            currentSelectionModeButton = button;
        }

    }

    private boolean isModeButtonSelected() {
        return currentSelectionModeButton != null;
    }

    private boolean isFileLoaded() {
        return timetableSheet != null;
    }

    private boolean isCourseSelected() {
        return selectionModeToDataMap.containsKey(SelectionMode.SELECT_COURSE);
    }

    //Resets all the mode buttons in a container.
    private void resetModeButtons(Pane container) {
        List<Node> selectionModeButtons = getModeButtons(container);

        for (Node node : selectionModeButtons) {
            SelectionModeButton button = (SelectionModeButton) node;
            button.setText(button.getTitle());
        }
    }

    private List<Node> getModeButtons(Pane container) {
        return container.getChildren().filtered(n -> n instanceof SelectionModeButton);
    }

    //Clears selected example info.
    private void clearExampleSelection(boolean includeCurrentModeButton) {

        if (includeCurrentModeButton) currentSelectionModeButton = null;

        /*Clear the map. If other course information was already selected,
         that information was built in reference to this certain course.
         Which you might be changing now. Thus the old information is now incorrect*/
        selectionModeToDataMap.clear();

        //reset mode buttons in the exampleSelectionControlBar.
        resetModeButtons(exampleSelectionControlBar);

    }

    //Clears operations done by the user related to courses such as search query text, results, and added courses.
    private void clearUserCourseOperations() {
        searchField.clear();
        searchResultList.clear();
        addedCoursesList.clear();
    }

    //Clears outdated form info.
    private void clearForm(boolean includeCurrentModeButton) {
        clearExampleSelection(includeCurrentModeButton);
        clearUserCourseOperations();
    }

    private void resetForm(boolean includeCurrentModeButton) {
        instructionLabel.setText(bundle.getString("chooseRemainingInfo"));
        clearForm(includeCurrentModeButton);
    }

    private File makeTempFile() {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("TA_TMP", null);
            tmpFile.deleteOnExit();
            System.out.println(tmpFile.getAbsolutePath());
        } catch (IOException io) {
            FXUtils.showAlert(bundle, "badTempFileIOHeader", "badTempFileIOBody");
        }
        return tmpFile;
    }

    //browse for a file.
    private File browseFile(Window parentWindow) {
        //New file chooser obj.
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));

        return chooser.showOpenDialog(parentWindow);

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
                String text = cell == null ? "" : cell.toString();
                //make a table sample label from the text
                Label tableSampleLabel = makeTableSampleLabel(text);
                //set click listener
                tableSampleLabel.setOnMouseClicked(e -> {
                    //ignore any clicks if a button is not selected
                    if (isModeButtonSelected()) {
                        putSelectedData(tableSampleLabel, currentSelectionModeButton.getMode());
                        giveSelectionFeedback(currentSelectionModeButton, tableSampleLabel, instructionLabel);
                        //Un-select the button
                        currentSelectionModeButton = null;
                    }
                });

                tableSample.add(tableSampleLabel, currentColumnIndex, currentRowIndex);

            }

        }
    }

    private Label makeTableSampleLabel(String text) {
        Label label = new Label(text);
        GridPane.setFillWidth(label, true);
        GridPane.setFillHeight(label, true);
        label.getStyleClass().add("tableSampleLabel");
        return label;
    }

    private void putSelectedData(Label tableSampleLabel, SelectionMode currentMode) {
        //Get the row and column of the label that was clicked.
        int rowIndex = GridPane.getRowIndex(tableSampleLabel);
        int columnIndex = GridPane.getColumnIndex(tableSampleLabel);

        //If the currently selected mode is for course cell selection.
        if (currentMode == SelectionMode.SELECT_COURSE) {
                    /* If other course information was already selected,
                    that information was built in reference to this certain course.
                    Which you might be changing now. Thus the old information is now incorrect,
                    this method was not called before, because resetting the form, will reset
                    the currently selected mode button, but that's needed in giveSelectionFeedback()*
                    */
            resetForm(false);

            selectionModeToDataMap.putCourseCellData(columnIndex, rowIndex);

            //If it's for course info cell selection.
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
                showAlert(bundle, "incorrectInfoHeader", "incorrectInfoBody");
            }
        }
    }

    /*Changes the text of the instructionLabel and the clicked button*/
    private void giveSelectionFeedback(SelectionModeButton button, Label tableSampleLabel, Label instructionLabel) {
        String instruction = isReadyToSearch() ? bundle.getString("allDone") : bundle.getString("chooseRemainingInfo");
        instructionLabel.setText(instruction);
        button.setText(button.getCurrentlySelectedPrefix(tableSampleLabel.getText()));
    }

    private boolean isReadyToSearch() {
        return !(selectionModeToDataMap.size() < getModeButtons(exampleSelectionControlBar).size());
    }

    //todo --- continue refactoring from here -------

    //Initializes controls that are related to adding courses.
    private void populateCourseOperationsGrid() {

        //-----------------------------------------------------------
        /*Label and list for showing courses that have been added from
        the list that contains available courses*/
        Label addedCoursesHeader = new Label(bundle.getString("addedCoursesHeader"));
        addedCoursesHeader.getStyleClass().add("Header");

        ListView<String> addedCourses = new ListView<>();
        addedCoursesList = FXCollections.observableArrayList();
        addedCourses.setItems(addedCoursesList);

        setRemoveItemOnClickListener(addedCourses);
        //--------------------------------------------------------


        //----------------------------------------------------------
        //Label and list for displaying courses that are available in the sheet.
        Label availableCoursesHeader = new Label(bundle.getString("availableCoursesHeader"));
        availableCoursesHeader.getStyleClass().add("Header");
        ListView<String> availableCourses = new ListView<>();
        searchResultList = FXCollections.observableArrayList();
        availableCourses.setItems(
                searchResultList);

        setAddItemOnClickListener(availableCourses, addedCoursesList);
        //------------------------------------------------------------


        //------------------------------------------------------------
        //Controls for searching
        searchField = new TextField();
        searchField.setPromptText(bundle.getString("searchFieldPrompt"));
        Button searchButton = new Button(bundle.getString("searchButton"));
        setSearchOnClickListener(searchButton, searchResultList, searchField);
        //--------------------------------------------------


        //-----------------------------------------------------------
        //generate table button
        Button generateButton = new Button(bundle.getString("generateButton"));

        setGenerateOnClickListener(generateButton, addedCoursesList);
        //------------------------------------------------------------


        //extra Box-------------------------------------
        //todo Might add option to change course details in this box.
        Label extrasBoxHeader = new Label("");
        //extrasBoxHeader.getStyleClass().add("Header");
        VBox extrasBox = makeExtrasBox();
        //---------------------------------------------

        //Adding controls to grid
        courseOperationsGrid.add(searchField, 0, 0);
        courseOperationsGrid.add(searchButton, 1, 0);
        courseOperationsGrid.add(availableCoursesHeader, 0, 1);
        courseOperationsGrid.add(addedCoursesHeader, 1, 1);
        courseOperationsGrid.add(availableCourses, 0, 2);
        courseOperationsGrid.add(addedCourses, 1, 2);
        courseOperationsGrid.add(generateButton, 0, 3, 3, 1);
        courseOperationsGrid.add(extrasBoxHeader, 2, 1);
        courseOperationsGrid.add(extrasBox, 2, 2);

    }


    //Returns a v box containing some pref controls...
    //todo allow editing course details in a future version
    private VBox makeExtrasBox() {
        VBox extraBox = new VBox();
        extraBox.getStyleClass().add("extrasBox");
        GridPane.setHgrow(extraBox, Priority.ALWAYS);
        return extraBox;
    }

    //Handle clicking generate..
    private void setGenerateOnClickListener(Button generateButton, ObservableList<String> generateFrom) {
        IntsBundle intBundle = (IntsBundle) ResourceBundle.getBundle(IntsBundle.class.getCanonicalName());
        generateButton.setOnAction(event -> {
            if (!generateFrom.isEmpty()) {
                DayToCourseListMap map = TableUtils.makeDayToCourseListMap(timetableSheet, selectionModeToDataMap, generateFrom);
                FXUtils.openWindow(bundle.getString("yourTimetable"), new Stage(),
                        intBundle.getInteger("windowWidth"), intBundle.getInteger("windowHeight"),
                        GeneratedTableController.class.getResource(GeneratedTableController.FXML_PATH),
                        ResourceBundle.getBundle(StringsBundle.class.getCanonicalName()),
                        new GeneratedTableController(map));
            } else
                showAlert(bundle, "notReadyToGenerateHeader", "notReadyToGenerateBody");
        });
    }

    //Handle clicking search...
    private void setSearchOnClickListener(Button searchButton, ObservableList<String> searchResultList, TextField searchField) {
        searchButton.setOnAction(event -> {
            if (isReadyToSearch()) {
                TableUtils.search(timetableSheet, searchResultList, searchField.getText());

            } else {
                showAlert(bundle, "insufficientInfoHeader", "insufficientInfoBody");
            }
        });
    }

    //Handle removing an item from a list view.
    private void setRemoveItemOnClickListener(ListView<String> removingFrom) {
        removingFrom.setOnMouseClicked(event -> {
            MultipleSelectionModel<String> sModel = removingFrom.getSelectionModel();
            String string = sModel.getSelectedItem();
            if (string != null)
                removingFrom.getItems()
                        .remove(sModel.getSelectedIndex());
        });
    }

    //Handle adding an item from a list view to a list of another.
    private void setAddItemOnClickListener(ListView<String> addingFrom, ObservableList<String> addingTo) {
        addingFrom.setOnMouseClicked(event -> {
            String clickedItem = addingFrom.getSelectionModel().getSelectedItem();
            if (!addingTo.contains(clickedItem) && clickedItem != null) {
                addingTo.add(clickedItem
                );
            }
        });

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
        chooseFileButton.setText(bundle.getString("chooseFileButton"));
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
                //todo in a future version, allow the user to row and col count
                populateTableSample(timetableSheet, 5, 5);
            }
            progressIndicator.setVisible(false);
            tableSample.setVisible(true);
            chooseFileButton.setDisable(false);

        }

        @Override
        public void onFailed(Throwable e) {
            FXUtils.showAlert(bundle, "badIOHeader", "badIOBody");
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
            FXUtils.showAlert(bundle, "badNetworkIOHeader", "badNetworkIOBody");
            taskFailedMode();

        }
    }


}
