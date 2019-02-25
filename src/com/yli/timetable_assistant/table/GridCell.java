package com.yli.timetable_assistant.table;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

 class GridCell extends Label {

     GridCell(String text){
         super(text);
         GridPane.setFillWidth(this,true);
         GridPane.setFillHeight(this,true);
        setTooltip(new Tooltip(text));
    }
}
