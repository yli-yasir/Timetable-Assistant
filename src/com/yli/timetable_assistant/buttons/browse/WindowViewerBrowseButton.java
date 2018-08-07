package com.yli.timetable_assistant.buttons.browse;


import com.yli.timetable_assistant.buttons.browse.WindowViewable;
import javafx.scene.control.Button;

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


}
