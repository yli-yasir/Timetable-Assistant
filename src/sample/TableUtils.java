package sample;

import javafx.collections.ObservableList;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

class TableUtils {


    static HashMap<SelectionStep,CellCoords> exampleCoords;

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

    static int[] makeColRowPair(int col, int row) {
        return new int[]{col, row};
    }

    static String makeStringValue(Cell cell) {
        return cell == null ? "" : cell.toString()
                .replace(" ", "").toLowerCase();
    }

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


    static String getDayString(Cell cell){

        new CellCoords(cell)
        return null;
    }

    static void drawTable() {
        int width = 842;
        int height = 595;
        int x = 0;
        int y = 0;
        int columnWidth = width / 6;

        BufferedImage image =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = image.createGraphics();
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.black);
        //draw column lines
        for (int i = 0; i < 5; i++) {
            x += columnWidth;
            g2d.drawLine(x, 0, x, height);
        }

        try {
            ImageIO.write(image, "png", new File("f.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void generateTimetable(Sheet sheet, ObservableList<String> courses) {
        LinkedHashMap<String,ArrayList<Course>> courseMap = new LinkedHashMap<>();
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (courses.contains(makeStringValue(cell))) {

                }
            }
        }
    }
}
