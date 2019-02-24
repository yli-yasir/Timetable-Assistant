package com.yli.timetable_assistant.exampleselection;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

final public class RowsColsComboBox extends ComboBox<Integer> {

    public void populate(int untilNumber, int defaultNumber, EventHandler<ActionEvent> onAction){
        //remove event handlers so they don't trigger onAction when we are populating them.
        setOnAction(null);
        ObservableList<Integer> numbers = getItems();
        //clear the lists since they might have been already populated.
        numbers.clear();
        //populate the rows list with numbers
        for (int i = 1; i <= untilNumber; i++)
            numbers.add(i);
        //set it to the default number
        setValue(defaultNumber);
        //add back the event handlers
        setOnAction(onAction);
    }
}
