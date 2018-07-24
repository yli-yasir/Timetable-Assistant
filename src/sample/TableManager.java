package sample;

import javafx.collections.ObservableList;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
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
    static Workbook readTimetable(File file) {
        Workbook timetable = null;
        try {
            timetable = WorkbookFactory.create(file);
        } catch (IOException | InvalidFormatException | NullPointerException e) {
            e.printStackTrace();
        }
        return timetable;
    }

    private static String makeStringValue(Cell cell) {
        return cell == null ? "" : cell.toString()
                .replace(" ", "").toLowerCase();
    }

    //Find the query and populates a list with the results.
    static void search(Sheet searchSheet, ObservableList<String> resultList, String searchQuery) {
        if (searchSheet != null) {
            HashSet<String> results = new HashSet<>();
            for (Row row : searchSheet) {
                for (Cell cell : row) {
                    String currentText = makeStringValue(cell);

                    if (currentText.contains(searchQuery.toLowerCase())) {
                        results.add(currentText);
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
    private static String getCourseInfo(Sheet sheet, Cell courseCell
            , SelectionMode mode) {
        //Get data about the requested information.
        SelectionModeData data = selectionModeToDataMap.get(mode);

        /*If the requested information is in a row, then we get this certain
        row (by using data.getValue()) then the information we want will be
        in the same column as the column of the row.
         */
        if (data.getType() == SelectionModeData.TYPE_ROW)
            return sheet.getRow(data.getValue()).getCell(courseCell.getColumnIndex()).toString();

        /*If the requested information is in a column, then we get the row of
        the course first, then we get the certain column in which the information
        was stored (by using data.getValue()). This will give us the information
        we want.*/
        else if (data.getType() == SelectionModeData.TYPE_COLUMN)
            return sheet.getRow(courseCell.getRowIndex()).getCell(data.getValue()).toString();

        else
            return null;
    }


    /**
     * Maps days as keys to courses as values, also sorts them in ascending order.
     *
     * @param sheet   The sheet you are looking for courses in.
     * @param courses A list of the courses, that we are looking to map.
     * @return a sorted map
     */
    private static LinkedHashMap<String, ArrayList<Course>> makeCourseDayMap(Sheet sheet, ObservableList<String> courses) {
        LinkedHashMap<String, ArrayList<Course>> courseMap = new LinkedHashMap<>();

        /*For each cell in the sheet, if it's for a course that's in the list
        of courses that we want, then add it to the hash map , with the day
        as a key, and add that course to the array list value for that key
         */
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (courses.contains(makeStringValue(cell))) {
                    String day = getCourseInfo(sheet, cell, SelectionMode.SELECT_DAY);

                    //Either get the list we already have or make a new one.
                    ArrayList<Course> dayCourses = courseMap.getOrDefault(day,
                            new ArrayList<>());

                    //Add the course to it.
                    dayCourses.add(new Course(makeStringValue(cell),
                            getCourseInfo(sheet, cell, SelectionMode.SELECT_TIME),
                            getCourseInfo(sheet, cell, SelectionMode.SELECT_HALL)));

                    courseMap.put(day, dayCourses);

                }
            }
        }
        return courseMap;
    }

    private static void drawTable(LinkedHashMap<String, ArrayList<Course>> map) {


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
        System.out.println("rows:" + rowCount);

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
            System.out.println("drawing col line");
            int currentX = (i * columnWidth);
            g2d.drawLine(currentX, 0, currentX, height);
        }

        //Draw row lines. Not using <=i is intentional.
        for (int i = 1; i < rowCount; i++) {
            System.out.println("drawing row line");
            int currentY = (i * rowHeight);
            g2d.drawLine(0, currentY, width, currentY);
        }

        //Draw strings
        g2d.setFont(new Font("Courier", Font.PLAIN, 12));
        FontMetrics metrics = g2d.getFontMetrics();
        int fontAscent = metrics.getAscent();

        int paddedX = 2;
        int paddedY = fontAscent;

        for (String day : map.keySet()) {
            //Draw day string.
            g2d.drawString(day, paddedX, paddedY);
            //Move down one cell.
            paddedY += rowHeight;
            for (Course course : map.get(day)) {
                String[] courseInfo = {course.getName(), course.getTime(), course.getHall()};
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
            ImageIO.write(image, "png", new File("f.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void generateTimetable(Sheet sheet, ObservableList<String> courses) {

        LinkedHashMap<String, ArrayList<Course>> courseDayMap =
                makeCourseDayMap(sheet, courses);

        drawTable(courseDayMap);

    }


}
