package sample;

import javafx.collections.ObservableList;
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

class TableUtils {


    static HashMap<SelectionStep, CellCoords> exampleCoords;

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




    /**
    Gets a string for a cell, that corresponds to a selection step for any
    by using the example data which was provided as a reference.

    --------------------EXAMPLE--------------------

     TLDR
     ---
     TYPICAL SHIFT = CellCoords(C) - CellCoords(O)
     REFERENCE SHIFT = CellCoords(C) - CellCoords(J)
     SHIFT = TYPICAL SHIFT - REFERENCE SHIFT
     CellCoords(O) = CellCoords(J) - (TYPICAL SHIFT - REFERENCE SHIFT)
     CellCoords(O) = CellCoords(J) - SHIFT
     ---

     N |O| N
    ----------
     N |J| N
    ----------
     N |C| N
    ----------
    In the example below:
        O = Corresponding info cell (see comment above).
        J = Any other cell.
        C = Will be used as reference.

       CellCoords(Row,Column)
       CellCoords(C) = (2,1)
       CellCoords(J) = (1,1)
       CellCoords(O) = (0,1)

       Typically to get O of C , you would have to move by the TYPICAL SHIFT from C.
       TYPICAL SHIFT = CellCoords(C) - CellCoords(O)

       So to confirm what was just said above:
       TYPICAL SHIFT = (2,1) - (0,1) = (2,0)

       CellCoords(C) - TYPICAL SHIFT = CellCoords(O)
       (2,1)         - (2,0)         = (0,1)        = CellCoords(O)

       REFERENCE SHIFT is how much you have to move to get from C to J.

       REFERENCE SHIFT = CellCoords(C) - CellCoords(J)
       REFERENCE SHIFT = (2,1)         - (1,1)         = (1,0)

       To find O of J (or any other cell):

       CellCoords(J) - (TYPICAL SHIFT - REFERENCE SHIFT) = CellCoords(O)

       (1,1)         - ( (2,0)        - (1,0)          ) =  ...
       (1,1)         - (1,0)                             = (0,1) = CellCoords(O)
       */
    static Cell getCorrespondingInfoCell(SelectionStep correspondingStep,Cell cell){
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
