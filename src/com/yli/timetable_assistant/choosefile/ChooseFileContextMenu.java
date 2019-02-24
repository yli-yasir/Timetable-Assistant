package com.yli.timetable_assistant.choosefile;

import javafx.scene.control.ContextMenu;

import java.util.ResourceBundle;

public class ChooseFileContextMenu extends ContextMenu {

    public ChooseFileContextMenu(ResourceBundle strings){
        //item 0
        LoadFromURLMenuItem loadFromURLMenuItem = new LoadFromURLMenuItem(strings);
        //item 1
        LoadFromComputerMenuItem loadFromComputerMenuItem = new LoadFromComputerMenuItem(strings);
        getItems().addAll(loadFromURLMenuItem, loadFromComputerMenuItem);
    }

    public LoadFromURLMenuItem getLoadFromURLMenuItem(){
        return (LoadFromURLMenuItem) getItems().get(0);
    }
    public LoadFromComputerMenuItem getLoadFromComputerMenuItem(){
        return (LoadFromComputerMenuItem) getItems().get(1);
    }
}
