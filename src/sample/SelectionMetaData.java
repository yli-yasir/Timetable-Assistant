package sample;

class SelectionMetaData {

    static  SelectionMetaMap selectionMetaMap = new SelectionMetaMap();

    static final int TYPE_ROW = 0;
    static final int TYPE_COLUMN = 1;

    /*The cell that was selected with the example data, is either
    part of a row or a column that contains the same sort of data.
    Say, a cell containing a time was selected. So this cell is either
    part of a row or a column that contains times.
    */
    private int type;

    /*If it's part of a row of the same sort of data, then we only care
    about saving the index of that specific row, since the column would be
    different for each other cell in the row, and the same thing applies if
    it's part of a column.
     */
    private int value;

    SelectionMetaData(int type, int value) {
        this.type = type;
        this.value = value;
    }

     int getType() {
        return type;
    }

     int getValue() {
        return value;
    }


}
