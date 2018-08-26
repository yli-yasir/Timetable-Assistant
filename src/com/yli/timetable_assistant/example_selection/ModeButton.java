package com.yli.timetable_assistant.example_selection;

import com.yli.timetable_assistant.example_selection.SelectionMode;
import com.yli.timetable_assistant.res.StringsBundle;
import javafx.scene.control.Button;

import java.util.ResourceBundle;

public class ModeButton extends Button {

    private static ResourceBundle stringsBundle = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());

    private SelectionMode mode;

    //localized string representation of what the user will select.
    //ex: "hall"  , "time"  etc...
    private String modeName;

    private String title;

    private String instruction;

    public ModeButton(SelectionMode mode) {
        super(stringsBundle.getString("titlePrefix") + stringsBundle.getString(mode.modeNameKey()));
        this.mode=mode;
        modeName = stringsBundle.getString(mode.modeNameKey());
        //example: titlePrefix = Choose , modeName = Day
        title = stringsBundle.getString("titlePrefix") + modeName;
        instruction = stringsBundle.getString("instructionPrefix") + modeName;

    }

    public String getTitle() {
        return title;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getCurrentlySelectedPrefix(String postfix) {
        return modeName + " : " + postfix;
    }


    public SelectionMode getMode() {
        return mode;
    }


}
