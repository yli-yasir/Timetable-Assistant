package com.yli.timetable_assistant.buttons.browse;

import javafx.scene.control.ChoiceBox;

public interface WindowViewable {
    ChoiceBox<Integer> getWindowRowsChoiceBox();
    ChoiceBox<Integer> getWindowColumnsChoiceBox();
}
