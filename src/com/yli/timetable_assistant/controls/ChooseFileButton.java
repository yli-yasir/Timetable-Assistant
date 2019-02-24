package com.yli.timetable_assistant.controls;

import com.yli.timetable_assistant.res.StringsBundle;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.ResourceBundle;

public class ChooseFileButton extends Button {


    public ChooseFileButton(ResourceBundle strings){
        setText(strings.getString("chooseFileButton"));
        setContextMenu(new ChooseFileContextMenu(strings));
        setOnAction(e -> getContextMenu().show(this, Side.BOTTOM, 0, 0));
    }
    public ChooseFileContextMenu getOwnMenu(){
        return (ChooseFileContextMenu) getContextMenu();
    }








}
