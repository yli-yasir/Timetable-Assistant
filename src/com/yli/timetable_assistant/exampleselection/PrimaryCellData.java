package com.yli.timetable_assistant.exampleselection;

/*Contains information about the cell that contains the course itself.*/

public class PrimaryCellData extends SelectionModeData {
    private int columnIndex;
    private int RowIndex;

    public PrimaryCellData(int columnIndex, int rowIndex) {
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
