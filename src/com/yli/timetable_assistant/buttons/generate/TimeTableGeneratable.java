package com.yli.timetable_assistant.buttons.generate;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public interface TimeTableGeneratable {
    TextField getOutputFileNameField();
    ChoiceBox<Integer> getFontSizeChoiceBox();
}
