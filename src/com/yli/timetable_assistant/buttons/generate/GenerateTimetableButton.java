package com.yli.timetable_assistant.buttons.generate;

import javafx.scene.control.Button;

public class GenerateTimetableButton extends Button {

    private TimeTableGeneratable timeTableGeneratable;

    GenerateTimetableButton(String text,TimeTableGeneratable timeTableGeneratable){
        super(text);
        this.timeTableGeneratable= timeTableGeneratable;
    }

    public String getOutputFileName(){
        return timeTableGeneratable.getOutputFileNameField().getText();
    }

    public int getFontSize(){
        return timeTableGeneratable.getFontSizeChoiceBox().getValue();
    }
}
