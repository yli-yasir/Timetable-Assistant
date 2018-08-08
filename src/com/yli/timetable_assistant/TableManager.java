package com.yli.timetable_assistant;

import com.yli.timetable_assistant.example_selection.SelectionMode;
import com.yli.timetable_assistant.example_selection.SelectionModeData;
import com.yli.timetable_assistant.example_selection.SelectionModeToDataMap;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

class TableManager {

    static SelectionModeToDataMap selectionModeToDataMap = new SelectionModeToDataMap();

    //Unpacks all merged cells in the sheet.
    static void unpackMergedCells(Sheet sheet) {
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
     * Returns a workbook of the file you provide.
     *
     * @param file the file to read.
     * @return null | Workbook containing the timetable
     */



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
    static void search(Sheet searchSheet, ObservableList<String> resultList, String searchQuery) {
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
            , SelectionMode mode) {
        //Get selectionModeData about the requested information.
        SelectionModeData selectionModeData = selectionModeToDataMap.get(mode);

        /*If the requested information is in a row, then we get this certain
        row (by using selectionModeData.getIndex()) then the information we want will be
        in the same column as the column of the row.
         */
        if (selectionModeData.getType() == SelectionModeData.TYPE_ROW) {

            Row row = sheet.getRow(selectionModeData.getIndex());
            //The rank is equal to the index of the column.
            int rank = courseCell.getColumnIndex();
            String string = makeStringValue(row.getCell(rank), true);
            return new RankedString(string, rank);

        }
        /*If the requested information is in a column, then we get the row of
        the course first, then we get the certain column in which the information
        was stored (by using selectionModeData.getIndex()). This will give us the information
        we want.*/
        if (selectionModeData.getType() == SelectionModeData.TYPE_COLUMN) {
            //The rank is equal to the index of the row.
            int rank = courseCell.getRowIndex();
            Row row = sheet.getRow(rank);
            String string = makeStringValue(row.getCell(selectionModeData.getIndex()), true);
            return new RankedString(string, rank);
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
    private static DayToCourseListMap makeDayToCourseListMap(Sheet sheet, ObservableList<String> addedCourses) {
        DayToCourseListMap dayToCourseListMap = new DayToCourseListMap();

        /*For each cell in the sheet, if it's for a course that's in the list
        of courses that we want, then add it to the hash map , with the day
        as a key, and add that course to the array list value for that key
         */
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (addedCourses.contains(makeStringValue(cell, true))) {
                    RankedString day = getCourseInfo(sheet, cell, SelectionMode.SELECT_DAY);

                    //Either get the list we already have or make a new one.
                    ArrayList<Course> dayCourses = dayToCourseListMap.getOrDefault(day,
                            new ArrayList<>());

                    //Add the course to it.
                    dayCourses.add(new Course(makeStringValue(cell, true),
                            getCourseInfo(sheet, cell, SelectionMode.SELECT_HALL),
                            getCourseInfo(sheet, cell, SelectionMode.SELECT_TIME))
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

    private static void drawTable(DayToCourseListMap map, float fontSize, String fileName) {

        //Image dimensions
        int width = 842;
        int height = 595;

        //Space between each line of strings.
        int lineSpacing = 20;

        //The number of columns is the number of the amount of days in the map.
        int columnCount = map.keySet().size();

        //The number of rows is the highest number of courses in any of the days.
        int rowCount = 0;
        for (ArrayList list : map.values()) {
            int size = list.size();
            if (size > rowCount) rowCount = size;
        }
        //Add an extra row that will contain the day header.
        rowCount += 1;

        /*Divide the width of the image by the number of needed columns to get
         to get the width of a single column*/
        int columnWidth = width / columnCount;

        /*Divide the height of the image by the number of needed rows to ge
         to get the height of a single row*/
        int rowHeight = height / rowCount;

        //Create an image.
        BufferedImage image =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        //Get graphics of the image.
        Graphics2D g2d = image.createGraphics();

        //Draw a white filled rectangle to color all of the image in white.
        g2d.fillRect(0, 0, width, height);

        //Change the color to black.
        g2d.setColor(Color.black);

        //Draw column lines. Not using <=i is intentional.
        for (int i = 1; i < columnCount; i++) {
            int currentX = (i * columnWidth);
            g2d.drawLine(currentX, 0, currentX, height);
        }

        //Draw row lines. Not using <=i is intentional.
        for (int i = 1; i < rowCount; i++) {
            int currentY = (i * rowHeight);
            g2d.drawLine(0, currentY, width, currentY);
        }

        //Draw strings
        g2d.setFont(g2d.getFont().deriveFont(fontSize));
        System.out.println(g2d.getFont().getSize());
        FontMetrics metrics = g2d.getFontMetrics();
        int fontAscent = metrics.getAscent();

        int paddedX = 2;
        int paddedY = fontAscent;

        for (RankedString day : map.keySet()) {
            //Draw day string.
            g2d.drawString(day.toString(), paddedX, paddedY);
            //Move down one cell.
            paddedY += rowHeight;
            for (Course course : map.get(day)) {
                String[] courseInfo = {course.getName(), course.getTime().toString(), course.getHall().toString()};
                for (int i = 0; i < courseInfo.length; i++) {
                    g2d.drawString(courseInfo[i], paddedX, paddedY + (i * lineSpacing));
                }
                //Move down one cell.
                paddedY += rowHeight;
            }
            //Move back to the top cell.
            paddedY = fontAscent;

            //Move to the right one cell.
            paddedX += columnWidth;
        }


        try {
            ImageIO.write(image, "png", new File(fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void generateTimetable(Sheet sheet, ObservableList<String> courses, float fontSize, String fileName) {
        DayToCourseListMap dayToCourseListMap =
                makeDayToCourseListMap(sheet, courses);
        drawTable(dayToCourseListMap, fontSize, fileName);

    }


}
