package com.yli.timetable_assistant.exampleselection;

public enum SelectionMode {

    /*Modes and their string key for getting the localized string of what
    they represent from the StringsBundle resource bundle class,
    there is critical use of this key in ModeButton class.*/
    SELECT_COURSE("course"),
    SELECT_DAY("day"),
    SELECT_TIME("time"),
    SELECT_HALL("hall");

    //This key will be used to grab strings from the stringsBundle.
    private String nameKey;

    SelectionMode(String nameKey){
        this.nameKey =nameKey;
    }

    public String nameKey(){
        return nameKey;
    }

}
