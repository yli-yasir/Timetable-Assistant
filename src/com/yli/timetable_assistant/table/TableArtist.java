package com.yli.timetable_assistant.table;

import com.yli.timetable_assistant.res.IntsBundle;
import sun.font.TrueTypeFont;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TableArtist {

     public static final int BASE_WIDTH=842;
     public static final int BASE_HEIGHT=595;

     public static final int DEFAULT_WIDTH=1754;
     public static final int DEFAULT_HEIGHT=1240;

     //Might want to consider allowing to only pass the width and then
    //calculate the height in order to maintain a specific aspect ratio...
    public static BufferedImage drawTable(DayToCourseListMap map,int width, int height, float fontSize) {


        int sizeMagnification = width /BASE_WIDTH;

        fontSize = fontSize * sizeMagnification;

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

        g2d.setColor(new Color(0, 105, 92));

        //Draw a  filled rectangle to color all of the image.
        g2d.fillRect(0, 0, width, height);

        //Change the color to white.
        g2d.setColor(Color.white);


        g2d.setStroke(new BasicStroke(sizeMagnification));

        //draw table lines...
        drawTableLines(g2d, rowCount, columnCount, rowHeight, columnWidth, height, width);

        g2d.setFont(g2d.getFont().deriveFont(Font.ITALIC, fontSize));


        drawTableInformation(g2d, map, rowHeight, columnWidth);

        g2d.dispose();

        return image;
    }

    private static void drawTableInformation(Graphics2D g2d, DayToCourseListMap map, int rowHeight, int columnWidth) {

        FontMetrics metrics = g2d.getFontMetrics();
        //find height of one line of text.
        int fontHeight = metrics.getHeight();

        //Keep track of loop times.
        int dayNo = 0;
        //For each day in the map.
        for (RankedString day : map.keySet()) {
            dayNo++;
            //----Drawing days---------------
            //get the day string.
            String dayName = day.toString();


            /*shift x by the column width multiplied by the (dayNo-1) (-1 because the first day doesn't
                        need shifting any shifting) to draw it in the proper cell for it.*/
            int xShift = columnWidth * (dayNo - 1);

            //Find the typical center coords for this day string in a cell,and draw it.*
            int centerX = findCenterX(columnWidth, metrics, dayName) + xShift;
            int centerY = findCenterY(rowHeight, metrics, dayName);
            g2d.drawString(dayName, centerX, centerY);
            //Get the course list for this day.
            ArrayList<Course> courseList = map.get(day);

            //-------Drawing courses in days-----------
            for (int courseNo = 1; courseNo <= courseList.size(); courseNo++) {
                //Get the course.
                Course course = courseList.get(courseNo - 1);
                //The lines of course info we are going to draw in each cell.
                String[] courseInfo = {course.getName(), course.getTime().toString(), course.getHall().toString()};
                /*Find center x and y coords typically in a cell, and shift y by the row
                height multiplied by the course number, and shift x by the (dayNo-1)so it gets drawn inside the appropriate cell.
                */
                int yShift = rowHeight * courseNo;
                int centerXC = findCenterX(columnWidth, metrics, courseInfo) + xShift;
                int centerYC = findCenterY(rowHeight, metrics, courseInfo) + yShift;
                //Draw lines of course info
                for (int lineNo = 0; lineNo < courseInfo.length; lineNo++) {
                    g2d.drawString(courseInfo[lineNo], centerXC, centerYC + (lineNo * fontHeight));
                }
            }

        }
    }

    private static int findCenterY(int cellHeight, FontMetrics metrics, String... stringLines) {
        /*return the center, added to the font ascent (So the whole string gets
        drawn below y , including it's ascent) minus half of the total height of the
        string lines so the block of text becomes centered*/
        return (cellHeight / 2 + metrics.getAscent()) - (metrics.getHeight() * stringLines.length) / 2;
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
