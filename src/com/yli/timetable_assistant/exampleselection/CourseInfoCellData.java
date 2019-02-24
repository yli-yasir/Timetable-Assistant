package com.yli.timetable_assistant.exampleselection;


/*Contains information on cells which have information about the course,
such as the time and place of the course*/
public class CourseInfoCellData extends SelectionModeData {


    public static final int TYPE_ROW = 0;
    public static final int TYPE_COLUMN = 1;

    /*The cell that was selected with the example data, is either
    part of a row or a column that contains the same sort of data.
    Say, a cell containing a time was selected. So this cell is either
    part of a row or a column that contains other time cells.
    */
    private int type;

    /*If it's part of a row of the same sort of data, then we only care
    about saving the index of that specific row, since the column would be
    different for each other cell in the row and we we should be able
    to figure it out. The same thing applies if it's part of a column.
     */
    private int index;

     CourseInfoCellData(int type, int index) {
        this.type = type;
        this.index = index;
    }

     public int getType() {
        return type;
    }

     public int getIndex() {
        return index;
    }


}
