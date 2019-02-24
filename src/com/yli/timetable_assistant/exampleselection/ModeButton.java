package com.yli.timetable_assistant.exampleselection;

import com.yli.timetable_assistant.res.StringsBundle;
import javafx.scene.control.ToggleButton;

import java.util.ResourceBundle;

//todo make this a Toggle Button
public class ModeButton extends ToggleButton {

    private static ResourceBundle stringsBundle = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());

    private SelectionMode mode;

    //The label of the text...
    //called original and saved in a different variable because
    //you might want to revert to it.
    private String originalLabel;

    public ModeButton(SelectionMode mode) {

        //set the mode that's triggered when it's toggled.
        this.mode = mode;

        //---------Make original label and set it on the button----------
        originalLabel = stringsBundle.getString("choose") +
                " " +
                stringsBundle.getString(mode.nameKey());

        setText(originalLabel);
        //----------------------------------------------------------------
    }


    public String getOriginalLabel() {
        return originalLabel;
    }

    //A description of what the button does, that is shown to the user.
    public String getInstruction() {
        return stringsBundle.getString("instructionPrefix") +
                stringsBundle.getString(mode.nameKey());
    }

    public SelectionMode getMode() {
        return mode;
    }


}
