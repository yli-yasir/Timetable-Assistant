package com.yli.timetable_assistant.buttons;


import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

public class WindowViewerBrowseButton extends Button {

    private WindowViewable windowViewable;

    public WindowViewerBrowseButton(String text,WindowViewable windowViewable){
        super(text);
        this.windowViewable=windowViewable;
    }

    public int getWindowRowCount(){
        return windowViewable.getWindowRowsChoiceBox().getValue();
    }

    public int getWindowColumnCount(){
        return windowViewable.getWindowColumnsChoiceBox().getValue();
    }


    public static interface WindowViewable {
        ChoiceBox<Integer> getWindowRowsChoiceBox();
        ChoiceBox<Integer> getWindowColumnsChoiceBox();
    }
}
