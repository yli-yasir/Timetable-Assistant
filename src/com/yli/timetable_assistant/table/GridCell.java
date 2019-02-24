package com.yli.timetable_assistant.table;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public class GridCell extends Label {

     GridCell(String text){
        super(text);
        GridPane.setFillWidth(this, true);
        GridPane.setFillHeight(this, true);
        getStyleClass().add("GridCell");
        setTooltip(new Tooltip(text));
    }
}
