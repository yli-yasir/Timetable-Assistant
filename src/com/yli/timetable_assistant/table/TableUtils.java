package com.yli.timetable_assistant.table;

import com.yli.timetable_assistant.exampleselection.*;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;


import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class TableUtils {


    public static Workbook readTable(File file) throws Exception {
        //The file that's passed must never be null!
        if (file == null) {
            throw new IllegalArgumentException("File to readTable passed must never be null");
        }

        //Using input stream to allow deletion of file (If file was tmp) on exit.
        return WorkbookFactory.create(new FileInputStream(file));
    }

    //Unpacks all merged cells in the sheet.
    public static void unpackMergedCells(Sheet sheet) {
        //For each merged region...
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {

            CellRangeAddress cellRange = sheet.getMergedRegion(i);

            int firstRow = cellRange.getFirstRow();
            int lastRow = cellRange.getLastRow();
            int firstColumn = cellRange.getFirstColumn();
            int lastColumn = cellRange.getLastColumn();

            //For each row that it spans across...
            for (int currentRow = firstRow; currentRow <= lastRow; currentRow++) {
                //For each column that it spans across that is within that row...
                for (int currentColumn = firstColumn; currentColumn <= lastColumn; currentColumn++) {
                    sheet.getRow(currentRow).getCell(currentColumn).setCellValue(
                            sheet.getRow(firstRow).getCell(firstColumn).toString());
                }
            }
        }

    }

    public static int getTableRowCount(Sheet timetableSheet){
        return timetableSheet.getPhysicalNumberOfRows();
    }

    public static int getTableColCount(Sheet timetableSheet){
        //iterate through the rows and find the widest one
        //return colCount for that one.
        int colCount = 0;
        for (Row row: timetableSheet){
            int rowCells = row.getPhysicalNumberOfCells();
            if (rowCells>colCount){
                colCount = rowCells;
            }
        }
        return colCount;
    }

    /**
     * If cell is null, returns an empty string.
     * Otherwise, removes all white spaces and returns a lower case string.
     *
     * @param cell to make the String from.
     * @return new String after performing operations.
     */
    public static String makeStringValue(Cell cell, boolean removeWhiteSpace) {
        if (cell == null) return "";
        String string = cell.toString();
        return makeStringValue(string, removeWhiteSpace);
    }

    private static String makeStringValue(String string, boolean removeWhiteSpace) {
        if (string == null || string.isEmpty()) return "";
        String resultString = string
                .toLowerCase().trim();
        return removeWhiteSpace ? resultString.replace(" ", "") :
                resultString;
    }

    //Find the query and populates a list with the results.
    public static void search(Sheet searchSheet, ObservableList<String> resultList, String searchQuery) {
        //lower case and remove white space from search query.
        searchQuery = makeStringValue(searchQuery, true);

        if (searchSheet != null) {
            HashSet<String> results = new HashSet<>();
            for (Row row : searchSheet) {
                for (Cell cell : row) {
                    /*lower case and remove white space from the string of the
                    current cell*/
                    String currentText = makeStringValue(cell, true);

                    /*Using contains because user might not have entered a completely
                    matching search query*/
                    if (currentText.contains(searchQuery)) {
                        /*add what we found to the results in lower case and
                        with white space*/
                        results.add(makeStringValue(cell, false));
                    }
                }
            }
            resultList.setAll(results);
            Collections.sort(resultList);
        }
    }

    /**
     * Get corresponding information that was stored in a selection mode.
     *
     * @param sheet      The sheet that contains the courseCell.
     * @param courseCell The cell that contains the course that we want information about.
     * @param mode       The mode when the information was stored.
     * @return String value of the requested information.
     */
    private static RankedString getCourseInfo(Sheet sheet, Cell courseCell
            , SelectionModeToDataMap selectionModeToDataMap, SelectionMode mode) {
        //Get selectionModeData about the requested information.
        SelectionModeData selectionModeData = selectionModeToDataMap.get(mode);

        /*Check if its SecondaryCellData, if it is then cast and proceed
        to get the info , it if isn't then return null*/
        if (selectionModeData instanceof SecondaryCellData) {
        /*If the requested information is in a row, then we get this certain
        row (by using selectionModeData.getIndex()) then the information we want will be
        in the same column as the column of the row.
         */
            SecondaryCellData courseInfoCellData = (SecondaryCellData) selectionModeData;
            if (courseInfoCellData.getType() == SecondaryCellData.TYPE_ROW) {
                Row row = sheet.getRow(courseInfoCellData.getIndex());
                //The rank is equal to the index of the column.
                int rank = courseCell.getColumnIndex();
                String string = makeStringValue(row.getCell(rank), false);
                //todo reconsider this line, it's used to replace blank info with "???"
                if (string.replace(" ", "").isEmpty()) {
                    string = "???";
                }
                return new RankedString(string, rank);

            }
        /*If the requested information is in a column, then we get the row of
        the course first, then we get the certain column in which the information
        was stored (by using selectionModeData.getIndex()). This will give us the information
        we want.*/
            else if (courseInfoCellData.getType() == SecondaryCellData.TYPE_COLUMN) {
                //The rank is equal to the index of the row.
                int rank = courseCell.getRowIndex();
                Row row = sheet.getRow(rank);
                String string = makeStringValue(row.getCell(courseInfoCellData.getIndex()), false);
                return new RankedString(string, rank);
            }
        }
        return null;
    }


    /**
     * Maps days as keys to courses as values, also sorts them in ascending order.
     *
     * @param sheet        The sheet you are looking for courses in.
     * @param addedCourses A list of the courses, that we are looking to map.
     * @return a sorted map
     */
    public static DayToCoursesMap makeDayToCourseListMap(Sheet sheet, SelectionModeToDataMap selectionModeToDataMap, ObservableList<String> addedCourses) {
        DayToCoursesMap dayToCoursesMap = new DayToCoursesMap();

        ArrayList<String> noWhiteSpaceCourseList = new ArrayList<>();

        //remove white space from added courses and add it to the other list...
        addedCourses.forEach( string ->
                noWhiteSpaceCourseList.add(string.replace(" ","")));

        /*For each cell in the sheet, if it's for a course that's in the list
        of courses that we want, then add it to the hash map , with the day
        as a key, and add that course to the array list value for that key
         */
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (noWhiteSpaceCourseList.contains(makeStringValue(cell, true))) {
                    RankedString day = getCourseInfo(sheet, cell, selectionModeToDataMap, SelectionMode.SELECT_DAY);

                    //Either get the list we already have or make a new one.
                    ArrayList<Course> dayCourses = dayToCoursesMap.getOrDefault(day,
                            new ArrayList<>());

                    //Add the course to it.
                    dayCourses.add(new Course(makeStringValue(cell, false),
                            getCourseInfo(sheet, cell, selectionModeToDataMap, SelectionMode.SELECT_HALL),
                            getCourseInfo(sheet, cell, selectionModeToDataMap, SelectionMode.SELECT_TIME),
                            day)
                    );


                    dayToCoursesMap.put(day, dayCourses);
                }
            }
        }
        /*Keys should be already sorted because it implements comparable
        ,Now we will sort all the array lists*/
        for (ArrayList<Course> dayCourses : dayToCoursesMap.values()) {
            dayCourses.sort((o1, o2) -> {
                if (o1.equals(o2)) return 0;
                int result = Integer.compare(o1.getTime().getRank(), o2.getTime().getRank());
                //To keep it consistent with equals, we can't permit to get a 0 for having similar time.
                return result == 0 ? 1 : result;
            });
        }
        return dayToCoursesMap;

    }

    public static void populateGeneratedTableGrid(GridPane generatedTableGrid, DayToCoursesMap dayToCoursesMap){

        //clear the grid in case it's already been populated
        generatedTableGrid.getChildren().clear();

        //Put the current column we are populating as userData in the grid
        generatedTableGrid.setUserData(0);

        //Add days at the top row first.
        dayToCoursesMap.forEach( (day, courses) ->
        {
            //Make a label array that represents the column
            //We are adding 1 because we are also going to add the day label
            //which is stored as the key at the top of the column.
            GeneratedTableCell[] column = new GeneratedTableCell[1+courses.size()];

            
            //populate the column
            //first add the day (the key)
            column[0] = new GeneratedTableCell(day.toString());

            //Next add all the remaining courses by looping
            for (int i = 1 ; i <= courses.size() ; i++){
                column[i] = new GeneratedTableCell(courses.get(i-1).toString());
            }

            //the current column index is stored in the user data
            generatedTableGrid.addColumn((int)generatedTableGrid.getUserData(),column);

            //keep track of the columns we have populated by increasing the
            //index stored in userData
            generatedTableGrid.setUserData((int)generatedTableGrid.getUserData() +  1 );

        }

        );

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
    public static void populateSampleTableGrid(GridPane tableSampleGrid, Sheet sheet, int rows, int columns
            , EventHandler<MouseEvent> CellClickHandler) {
        System.out.println("populating" + rows + " " + columns);

        //Clear the grid first since it might have been already populated with other children.
        tableSampleGrid.getChildren().clear();

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
                SampleTableCell gridCell = new SampleTableCell(text);
                gridCell.setOnMouseClicked(CellClickHandler);
                tableSampleGrid.add(gridCell, currentColumnIndex, currentRowIndex);

            }

        }
    }

}
