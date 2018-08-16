package com.yli.timetable_assistant.buttons;

import com.yli.timetable_assistant.example_selection.SelectionMode;
import com.yli.timetable_assistant.res.StringsBundle;
import javafx.scene.control.Button;

import java.util.ResourceBundle;

public class SelectionModeButton extends Button {

    private static ResourceBundle StringsBundle = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());

    private SelectionMode mode;

    //localized string representation of what the user will select.
    //ex: "hall"  , "time"  etc...
    private String modeName;

    private static String titlePrefix = StringsBundle.getString("titlePrefix");

    private static String instructionPrefix = StringsBundle.getString("instructionPrefix");


    public SelectionModeButton(SelectionMode mode, String modeName) {
        super(titlePrefix+modeName);
        this.mode = mode;
        this.modeName = modeName;
    }

    public String getTitle(){
        return titlePrefix+ modeName;
    }
    public String getInstruction() {
        return instructionPrefix + modeName;
    }

    public String getCurrentlySelectedPrefix(String postfix){
        return modeName + " : " + postfix;
    }


    public SelectionMode getMode() {
        return mode;
    }


}
