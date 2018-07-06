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

class TableUtils {

    static SelectionMetaMap selectionMetaMap = new SelectionMetaMap();

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

    //WorkbookFactory.create(File f) wrapped in in try/catch.
    static Workbook readTimetable(File file) {
        Workbook timetable = null;
        try {
            timetable = WorkbookFactory.create(file);
        } catch (IOException | InvalidFormatException e) {
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

    private static String getCourseInfo(Sheet sheet, Cell courseCell
            , SelectionStep step) {
        SelectionMetaData data = selectionMetaMap.get(step);

        if (data.getType() == SelectionMetaData.TYPE_ROW)
            return sheet.getRow(data.getValue()).getCell(courseCell.getColumnIndex()).toString();

        else if (data.getType() == SelectionMetaData.TYPE_COLUMN)
            return sheet.getRow(courseCell.getRowIndex()).getCell(data.getValue()).toString();

        else
            return courseCell.toString();
    }

    private static LinkedHashMap<String, ArrayList<Course>> getCourseDayMap(Sheet sheet, ObservableList<String> courses) {
        LinkedHashMap<String, ArrayList<Course>> courseMap = new LinkedHashMap<>();

        /*For each cell in the sheet, if it's for a course that's in the list
        of courses that we want, then add it to the hash map , with the day
        as a key, and add that course to the array list value for that key
         */
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (courses.contains(makeStringValue(cell))) {
                    String day = getCourseInfo(sheet, cell, SelectionStep.SELECT_DAY);

                    //Either get the list we already have or make a new one.
                    ArrayList<Course> dayCourses = courseMap.getOrDefault(day,
                            new ArrayList<>());

                    //Add the course to it.
                    dayCourses.add(new Course(cell.toString(),
                            getCourseInfo(sheet, cell, SelectionStep.SELECT_TIME),
                            getCourseInfo(sheet, cell, SelectionStep.SELECT_HALL)));

                    courseMap.put(day, dayCourses);

                }
            }
        }
        return courseMap;
    }

    private static void drawBoundString(Graphics2D g2d,int x,int y,String s,int boundWidth){
        FontMetrics metrics = g2d.getFontMetrics();
        int originalSize = metrics.getFont().getSize();
        for (int currentSize = originalSize; metrics.stringWidth(s)>boundWidth;currentSize--){
            g2d.setFont(metrics.getFont().deriveFont((float)currentSize));
            metrics=g2d.getFontMetrics();
        }
        g2d.drawString(s,x,y);
    }
    private static void drawTable(LinkedHashMap<String, ArrayList<Course>> map) {

        System.out.println("map size is "  + map.size());


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
        //Add an extra row that will contain the day.
        rowCount+=1;
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
        for (int i = 1; i < rowCount ; i++) {
            System.out.println("drawing row line");
            int currentY = (i * rowHeight);
            g2d.drawLine(0, currentY, width, currentY);
        }

        //Draw strings
        g2d.setFont(new Font("Courier",Font.PLAIN,12));
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
            paddedY=fontAscent;

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
                getCourseDayMap(sheet, courses);

        drawTable(courseDayMap);

    }


}
