package sample;

import java.util.HashMap;

class SelectionMetaMap{

    private HashMap<SelectionStep, SelectionMetaData> exampleMap;



    /*this method must be called before calling putOtherMetaData() and since
    a course cell isn't part of a row or column that contains the same sort
    of data we pass -1 as the type.
     */
    final void putCourseMetaData(int exampleRowIndex) {
        exampleMap.put(SelectionStep.SELECT_COURSE,
                new SelectionMetaData(-1, exampleRowIndex));
    }

    /*Compares it against the example course and decides how to properly make
      meta data.*/
     void putOtherMetaData(int exampleRowIndex,int exampleColumnIndex,SelectionStep step) throws ExampleCourseNotSetException {
        if (!exampleMap.containsKey(SelectionStep.SELECT_COURSE)) {
            throw new ExampleCourseNotSetException("Example course was not set!");
        } else {
            SelectionMetaData courseMeta = exampleMap.get(SelectionStep.SELECT_COURSE);

            /*If the example data row is the same as the row of the course
            then there is probably a column of that data to the left or right
            of the example course.

            If it's not on the same row then there is probably a column of that
            data to the left or right
            */
            int type = exampleRowIndex == courseMeta.getValue()?
                    SelectionMetaData.TYPE_COLUMN : SelectionMetaData.TYPE_ROW;

            /*If it's of type row then save the value for row and same
            goes for column */
            int value = type == SelectionMetaData.TYPE_ROW ?
                    exampleRowIndex : exampleColumnIndex;

            //Insert the new meta data object
            exampleMap.put(step,new SelectionMetaData(type,value));

        }

    }




}
