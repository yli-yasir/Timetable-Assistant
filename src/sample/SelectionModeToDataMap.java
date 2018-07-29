package sample;

import java.util.HashMap;

//encapsulates a certain kind of HashMap and provides utility methods.
class SelectionModeToDataMap {

    private HashMap<SelectionMode, SelectionModeData> exampleMap = new HashMap<>();



    /*this method must be called before calling putCourseInfoMetaData() and since
    a course cell isn't part of a row or column that contains the same sort
    of data we pass -1 as the type.
     */
    final void puSelectionModeData(int exampleRowIndex) {
        exampleMap.put(SelectionMode.SELECT_COURSE,
                new SelectionModeData(-1, exampleRowIndex));
    }

    /*Compares it against the example course and decides how to properly make
      meta data.*/
     final void putCourseInfoMetaData(int exampleRowIndex, int exampleColumnIndex, SelectionMode step) throws ExampleCourseNotSetException {
         //If puSelectionModeData has not been called first...
        if (!exampleMap.containsKey(SelectionMode.SELECT_COURSE)) {
            throw new ExampleCourseNotSetException("Example course was not set!");
        } else {
            //get the selected course meta data to use it.
            SelectionModeData courseMeta = exampleMap.get(SelectionMode.SELECT_COURSE);

            /*If the example data row is the same as the row of the course
            then there is probably a column of that data to the left or right
            of the example course.

            If it's not on the same row then there is probably a column of that
            data to the left or right
            */
            int type = exampleRowIndex == courseMeta.getIndex()?
                    SelectionModeData.TYPE_COLUMN : SelectionModeData.TYPE_ROW;

            /*If it's of type row then save the value for row and same
            goes for column*/
            int value = type == SelectionModeData.TYPE_ROW ?
                    exampleRowIndex : exampleColumnIndex;

            //Insert the new meta data object.
            exampleMap.put(step,new SelectionModeData(type,value));

        }

    }

    int size(){
         return exampleMap.size();
    }

    SelectionModeData get(SelectionMode key){
         return exampleMap.get(key);
    }

    boolean containsKey(SelectionMode step){
         return exampleMap.containsKey(step);
    }


}
