package com.yli.timetable_assistant.example_selection;

import com.yli.timetable_assistant.res.StringsBundle;

import java.util.ResourceBundle;

public enum SelectionMode {

    /*Modes and their string key for getting the localized string of what
    they represent from the StringsBundle resource bundle class,
    there is critical use of this key in SelectionModeButton class.*/
    SELECT_COURSE("selectCourseModeName"),
    SELECT_DAY("selectDayModeName"),
    SELECT_TIME("selectTimeModeName"),
    SELECT_HALL("selectHallModeName");

    private String modeNameKey;

    SelectionMode(String modeNameKey){
        this.modeNameKey=modeNameKey;
    }

    public String modeNameKey(){
        return modeNameKey;
    }

}
