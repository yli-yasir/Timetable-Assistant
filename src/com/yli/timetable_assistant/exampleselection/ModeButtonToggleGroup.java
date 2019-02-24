package com.yli.timetable_assistant.exampleselection;

import com.sun.istack.internal.Nullable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleGroup;


public class ModeButtonToggleGroup extends ToggleGroup {

    public ModeButtonToggleGroup(EventHandler<ActionEvent> onButtonClick){
        //make as many buttons as needed for selection modes, in here I am making
        //a button for each mode.
        for (SelectionMode mode : SelectionMode.values()) {

            ModeButton button = new ModeButton(mode);

            button.setOnAction(onButtonClick);

            button.setToggleGroup(this);
        }
    }
    public ModeButton getToggledModeButton() {
        return (ModeButton)getSelectedToggle();
    }

    //Is there any button that's toggled?
    public boolean isButtonToggled() {
        return getSelectedToggle() != null;
    }

    public int getToggledModeButtonIndex(){
        return getToggles().indexOf(
                getSelectedToggle());
    }
    public boolean isLastButtonToggled(){
        return getToggledModeButtonIndex() + 1 == getToggles().size();
    }

    @Nullable
    public ModeButton getNextButton(){
        if(!isLastButtonToggled()) {
            return (ModeButton) getToggles().get(getToggledModeButtonIndex() + 1);
        }
        else{
            return null;
        }
    }



}
