package com.yli.timetable_assistant.example_selection;

/*Contains information about the cell that contains the course itself.*/

public class CourseCellData extends SelectionModeData {
    private int columnIndex;
    private int RowIndex;

    public CourseCellData(int columnIndex, int rowIndex) {
        this.columnIndex = columnIndex;
        RowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getRowIndex() {
        return RowIndex;
    }
}
