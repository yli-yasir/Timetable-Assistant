package com.yli.timetable_assistant.buttons;

import com.yli.timetable_assistant.example_selection.SelectionMode;
import javafx.scene.control.Button;

 public class SelectionModeButton extends Button {

    private SelectionMode step;

    public SelectionModeButton(String text, SelectionMode step) {
        super(text);
        this.step= step;
    }


     public SelectionMode getMode() {
        return step;
    }

}
