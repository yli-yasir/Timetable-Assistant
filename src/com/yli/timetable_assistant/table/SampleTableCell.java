package com.yli.timetable_assistant.table;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public class SampleTableCell extends GridCell {

     SampleTableCell(String text){
        super(text);
        getStyleClass().add("SampleTableCell");
    }
}
