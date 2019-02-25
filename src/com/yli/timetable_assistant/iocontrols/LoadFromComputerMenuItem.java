package com.yli.timetable_assistant.iocontrols;

import com.yli.timetable_assistant.utils.FXUtils;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.util.ResourceBundle;
import java.util.function.Consumer;

final public class LoadFromComputerMenuItem extends MenuItem {

    private Consumer<File> onReceiveResult;

     LoadFromComputerMenuItem(ResourceBundle strings){
        setText(strings.getString("loadFromPC"));
        setOnAction(e -> {
            File file = FXUtils.browseFile(getParentPopup().getOwnerWindow());
            //If a file was indeed chosen.
            if (file != null && onReceiveResult!=null) {
                onReceiveResult.accept(file);
            }
        });
    }
    public void setOnReceiveResult(Consumer<File> action){
        onReceiveResult = action;
    }

}
