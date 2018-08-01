package com.yli.timetable_assistant.example_selection;

import javafx.scene.control.Button;

 public class SelectionModeButton extends Button {

    private SelectionMode step;

    public SelectionModeButton(String text, SelectionMode step) {
        super(text);
        this.step= step;
    }


     public SelectionMode getStep() {
        return step;
    }

}
