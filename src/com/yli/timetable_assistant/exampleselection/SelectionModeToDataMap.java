package com.yli.timetable_assistant.exampleselection;

import java.util.HashMap;

//encapsulates a certain kind of HashMap and provides utility methods.
public class SelectionModeToDataMap {

    private HashMap<SelectionMode, SelectionModeData> exampleMap = new HashMap<>();



    /*this method must be called before calling putDataOfSecondaryCell() and since
    a course cell isn't part of a row or column that contains the same sort
    of data we pass -1 as the type, and save the selected row index to use it to figure out
    if the course info cell are located in a row or column.
     */
    public final void putDataOfPrimaryCell(int columnIndex, int rowIndex) {
        exampleMap.put(SelectionMode.SELECT_COURSE,
                new PrimaryCellData(columnIndex,rowIndex));
    }

    /*Compares it against the example course and decides how to properly make
      selection data.*/
     public final void putDataOfSecondaryCell(int columnIndex, int rowIndex, SelectionMode mode) throws ExampleCourseNotSetException, IncorrectExampleInfoException {
         //If putDataOfPrimaryCell has not been called first...
        if (!exampleMap.containsKey(SelectionMode.SELECT_COURSE)) {
            throw new ExampleCourseNotSetException("Example course was not set!");

        } else {
            /*get the selected course cell data to use it infer if the info cell is taking
            part of a column or a row*/
            PrimaryCellData primaryCellData = (PrimaryCellData) exampleMap.get(SelectionMode.SELECT_COURSE);


            /*This will be used to indicate if it's in a cell or a row, using
            the constants stored in the SecondaryCellData class but for now it's unknown */
            int type;

            /*If the example data row is the same as the row of the course
            then there is probably a column of that data to the left or right
            of the example course, since their can't be a row or it would intersect
            the course cell.*/

            if (rowIndex== primaryCellData.getRowIndex()){
                type= SecondaryCellData.TYPE_COLUMN;
            }
            /* If  it has the same column index then there is a row
            directly above or below it since their can't be
            a column or it would intersect the course cell.
             */
            else if (columnIndex== primaryCellData.getColumnIndex()){
                type= SecondaryCellData.TYPE_ROW;
            }
            /*If it's not directly above it or below it , an incorrect info
             cell must have been selected
              */
            else{
                throw new IncorrectExampleInfoException("Incorrect info cell...");
            }

            /*If it's of type row then save the value for row and same
            goes if it was of type column*/
            int value = type == SecondaryCellData.TYPE_ROW ?
                    rowIndex : columnIndex;

            //Insert the new course cell info object.
            exampleMap.put(mode,new SecondaryCellData(type,value));

        }

    }

    public int size(){
         return exampleMap.size();
    }

    public SelectionModeData get(SelectionMode key){
         return exampleMap.get(key);
    }

    public boolean containsKey(SelectionMode step){
         return exampleMap.containsKey(step);
    }

    public void clear(){
         exampleMap.clear();
    }

    public boolean isCourseSelected() {
        return containsKey(SelectionMode.SELECT_COURSE);
    }



}
