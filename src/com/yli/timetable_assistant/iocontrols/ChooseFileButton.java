package com.yli.timetable_assistant.iocontrols;

import javafx.geometry.Side;
import javafx.scene.control.Button;

import java.util.ResourceBundle;

public class ChooseFileButton extends Button {


    public ChooseFileButton(ResourceBundle strings){
        setText(strings.getString("chooseFileButton"));
        setContextMenu(new ChooseFileContextMenu(strings));
        getStyleClass().add("ChooseFileButton");
        setOnAction(e -> getContextMenu().show(this, Side.BOTTOM, 0, 0));
    }
    public ChooseFileContextMenu getOwnMenu(){
        return (ChooseFileContextMenu) getContextMenu();
    }








}
