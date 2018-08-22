package com.yli.timetable_assistant.table;

import com.yli.timetable_assistant.example_selection.SelectionMode;
import com.yli.timetable_assistant.example_selection.CourseInfoCellData;
import com.yli.timetable_assistant.example_selection.SelectionModeData;
import com.yli.timetable_assistant.example_selection.SelectionModeToDataMap;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;


import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class TableUtils {


    public static Workbook readTable(File file) throws Exception{
        //The file that's passed must never be null!
        if (file==null){
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

    /**
     * If cell is null, returns an empty string.
     * Otherwise, removes all white spaces and returns a lower case string.
     *
     * @param cell to make the String from.
     * @return new String after performing operations.
     */
    private static String makeStringValue(Cell cell, boolean whiteSpace) {
        if (cell == null) return "";
        String string = cell.toString();
        return makeStringValue(string, whiteSpace);
    }

    private static String makeStringValue(String string, boolean whiteSpace) {
        if (string == null || string.isEmpty()) return "";
        String resultString = string
                .toLowerCase().trim();
        return !whiteSpace ? resultString.replace(" ", "") :
                resultString;
    }

    //Find the query and populates a list with the results.
    public static void search(Sheet searchSheet, ObservableList<String> resultList, String searchQuery) {
        //lower case and remove white space from search query.
        searchQuery = makeStringValue(searchQuery, false);

        if (searchSheet != null) {
            HashSet<String> results = new HashSet<>();
            for (Row row : searchSheet) {
                for (Cell cell : row) {
                    /*lower case and remove white space from the string of the
                    current cell*/
                    String currentText = makeStringValue(cell, false);

                    /*Using contains because user might not have entered a completely
                    matching search query*/
                    if (currentText.contains(searchQuery)) {
                        /*add what we found to the results in lower case and
                        with white space*/
                        results.add(makeStringValue(cell, true));
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

        /*Check if its CourseInfoCellData, if it is then cast and proceed
        to get the info , it if isn't then return null*/
        if (selectionModeData instanceof CourseInfoCellData ){
        /*If the requested information is in a row, then we get this certain
        row (by using selectionModeData.getIndex()) then the information we want will be
        in the same column as the column of the row.
         */
            CourseInfoCellData courseInfoCellData = (CourseInfoCellData) selectionModeData;
            if (courseInfoCellData.getType() == CourseInfoCellData.TYPE_ROW) {
                Row row = sheet.getRow(courseInfoCellData.getIndex());
                //The rank is equal to the index of the column.
                int rank = courseCell.getColumnIndex();
                String string = makeStringValue(row.getCell(rank), true);
                //todo reconsider this line, it's used to replace blank info with "???"
                if (string.replace(" ","").isEmpty()){
                    string="???";
                }
                return new RankedString(string, rank);

            }
        /*If the requested information is in a column, then we get the row of
        the course first, then we get the certain column in which the information
        was stored (by using selectionModeData.getIndex()). This will give us the information
        we want.*/
        if (courseInfoCellData.getType() == CourseInfoCellData.TYPE_COLUMN) {
            //The rank is equal to the index of the row.
            int rank = courseCell.getRowIndex();
            Row row = sheet.getRow(rank);
            String string = makeStringValue(row.getCell(courseInfoCellData.getIndex()), true);
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
    public static DayToCourseListMap makeDayToCourseListMap(Sheet sheet, SelectionModeToDataMap selectionModeToDataMap, ObservableList<String> addedCourses) {
        DayToCourseListMap dayToCourseListMap = new DayToCourseListMap();

        /*For each cell in the sheet, if it's for a course that's in the list
        of courses that we want, then add it to the hash map , with the day
        as a key, and add that course to the array list value for that key
         */
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (addedCourses.contains(makeStringValue(cell, true))) {
                    RankedString day = getCourseInfo(sheet, cell, selectionModeToDataMap, SelectionMode.SELECT_DAY);

                    //Either get the list we already have or make a new one.
                    ArrayList<Course> dayCourses = dayToCourseListMap.getOrDefault(day,
                            new ArrayList<>());

                    //Add the course to it.
                    dayCourses.add(new Course(makeStringValue(cell, true),
                            getCourseInfo(sheet, cell, selectionModeToDataMap, SelectionMode.SELECT_HALL),
                            getCourseInfo(sheet, cell, selectionModeToDataMap, SelectionMode.SELECT_TIME))
                    );


                    dayToCourseListMap.put(day, dayCourses);
                }
            }
        }
        /*Keys should be already sorted because it implements comparable
        ,Now we will sort all the array lists*/
        for (ArrayList<Course> dayCourses : dayToCourseListMap.values()) {
            dayCourses.sort((o1, o2) -> {
                if (o1.equals(o2)) return 0;
                int result = Integer.compare(o1.getTime().getRank(), o2.getTime().getRank());
                //To keep it consistent with equals, we can't permit to get a 0 for having similar time.
                return result == 0 ? 1 : result;
            });
        }
        return dayToCourseListMap;

    }


}