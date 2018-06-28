package sample;

import org.apache.poi.ss.usermodel.Cell;

public class CellCoords {

    private int rowIndex;
    private int columnIndex;

     CellCoords(Cell cell) {
        this.rowIndex = cell.getRowIndex();
        this.columnIndex = cell.getColumnIndex();
    }

    CellCoords(int rowIndex, int columnIndex) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    void minus(Cell other){
         setRowIndex(rowIndex-other.getRowIndex());
         setColumnIndex(columnIndex-other.getColumnIndex());
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }
}
