package com.yli.timetable_assistant;

import com.yli.timetable_assistant.example_selection.SelectionMode;
import com.yli.timetable_assistant.example_selection.SelectionModeData;
import com.yli.timetable_assistant.example_selection.SelectionModeToDataMap;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;


import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.*;

class TableManager {


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
            , SelectionModeToDataMap selectionModeToDataMap, SelectionMode mode) {
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
    static DayToCourseListMap makeDayToCourseListMap(Sheet sheet, SelectionModeToDataMap selectionModeToDataMap, ObservableList<String> addedCourses) {
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

    static BufferedImage drawTable(DayToCourseListMap map, float fontSize) {

        int fontSizeMagnification= 2;
        fontSize = fontSize * fontSizeMagnification;

        //Image dimensions
        int width = 1745;
        int height = 1240;

        //The number of columns is the number of the amount of days in the map.
        int columnCount = findColumnCount(map);

        //The number of rows is the highest number of courses in any of the days.
        int rowCount = findRowCount(map);

        /*Divide the width of the image by the number of needed columns to get
         to get the width of a single column*/
        int columnWidth = width / columnCount;

        /*Divide the height of the image by the number of needed rows to ge
         to get the height of a single row*/
        int rowHeight = height / rowCount;

        //Create an image.
        BufferedImage image =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        //Get graphics of the image.
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(new Color(0,105,92));

        //Draw a  filled rectangle to color all of the image.
        g2d.fillRect(0, 0, width, height);

        //Change the color to white.
        g2d.setColor(Color.white);


        //draw table lines...
        drawTableLines(g2d, rowCount, columnCount, rowHeight, columnWidth, height, width);

        //Draw strings
        g2d.setFont(g2d.getFont().deriveFont(Font.ITALIC,fontSize));


        drawTableInformation(g2d,map,rowHeight,columnWidth);

        return image;
    }

    private static void drawTableInformation(Graphics2D g2d, DayToCourseListMap map,int rowHeight,int columnWidth){

        FontMetrics metrics = g2d.getFontMetrics();
        //find height of one line of text.
        int fontHeight = metrics.getHeight();

        //Keep track of loop times.
        int dayNo=0;
        //For each day in the map.
        for (RankedString day : map.keySet()) {
            dayNo++;
            //----Drawing days---------------
            //get the day string.
            String dayName = day.toString();


            /*shift x by the column width multiplied by the (dayNo-1) (-1 because the first day doesn't
                        need shifting any shifting) to draw it in the proper cell for it.*/
            int xShift = columnWidth * (dayNo-1);

            //Find the typical center coords for this day string in a cell,and draw it.*
            int centerX = findCenterX(columnWidth, metrics, dayName) + xShift;
            int centerY = findCenterY(rowHeight, metrics, dayName);
            g2d.drawString(dayName, centerX, centerY);
            //Get the course list for this day.
            ArrayList<Course> courseList = map.get(day);

            //-------Drawing courses in days-----------
            for (int courseNo = 1; courseNo <= courseList.size(); courseNo++) {
                //Get the course.
                Course course = courseList.get(courseNo-1);
                //The lines of course info we are going to draw in each cell.
                String[] courseInfo = {course.getName(), course.getTime().toString(), course.getHall().toString()};
                /*Find center x and y coords typically in a cell, and shift y by the row
                height multiplied by the course number, and shift x by the (dayNo-1)so it gets drawn inside the appropriate cell.
                */
                int yShift= rowHeight*courseNo;
                int centerXC= findCenterX(columnWidth,metrics,courseInfo) + xShift;
                int centerYC= findCenterY(rowHeight,metrics,courseInfo) + yShift;
                //Draw lines of course info
                for (int lineNo = 0; lineNo < courseInfo.length; lineNo++) {
                    g2d.drawString(courseInfo[lineNo],centerXC,centerYC + (lineNo * fontHeight));
                }
            }

        }
    }

    private static int findCenterY(int cellHeight, FontMetrics metrics, String... stringLines) {
        /*return the center, added to the font ascent (So the whole string gets
        drawn below y , including it's ascent) minus half of the total height of the
        string lines so the block of text becomes centered*/
        return ( cellHeight / 2 + metrics.getAscent() )- ( metrics.getHeight()* stringLines.length)/2;
    }

    private static int findCenterX(int cellWidth, FontMetrics metrics, String... stringLines) {
        //If we have a block of text, then we find the one with the max width.
        int maxWidth = metrics.stringWidth(stringLines[0]);
        for (String s : stringLines) {
            int stringWidth = metrics.stringWidth(s);
            if (stringWidth > maxWidth) {
                maxWidth = stringWidth;
            }
        }
        //Find the x center of the column, then move back by half of the string width.
        return cellWidth / 2 - maxWidth / 2;
    }

    private static int findRowCount(DayToCourseListMap map) {
        //The number of rows is the highest number of courses in any of the days.
        int rowCount = 0;
        for (ArrayList list : map.values()) {
            int size = list.size();
            if (size > rowCount) rowCount = size;
        }

        //Add an extra row that will contain the day header.
        return rowCount + 1;
    }

    private static int findColumnCount(DayToCourseListMap map) {
        //The number of columns is the number of the amount of days in the map.
        return map.keySet().size();
    }

    private static void drawTableLines(Graphics2D g2d, int rowCount, int columnCount, int rowHeight, int columnWidth, int imageHeight, int imageWidth) {
        g2d.setStroke(new BasicStroke(4));
        drawColumnLines(g2d, columnCount, columnWidth, imageHeight);
        drawRowLines(g2d, rowCount, rowHeight, imageWidth);
    }

    private static void drawColumnLines(Graphics2D g2d, int columnCount, int columnWidth, int imageHeight) {
                /*Draw column lines. Not using <=i is intentional, since the
        last column will not need to be closed off from both sides.
         */
        for (int i = 1; i < columnCount; i++) {
            int currentX = (i * columnWidth);
            g2d.drawLine(currentX, 0, currentX, imageHeight);
        }
    }

    private static void drawRowLines(Graphics2D g2d, int rowCount, int rowHeight, int imageWidth) {
        /*Draw row lines. Not using <=i is intentional, since the
        last column will not need to be closed off from both sides.
         */
        for (int i = 1; i < rowCount; i++) {
            int currentY = (i * rowHeight);
            g2d.drawLine(0, currentY, imageWidth, currentY);
        }
    }
}
