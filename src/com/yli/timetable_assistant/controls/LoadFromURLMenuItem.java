package com.yli.timetable_assistant.controls;

import com.yli.timetable_assistant.res.Integers;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

import java.util.ResourceBundle;
import java.util.function.Consumer;

final public class LoadFromURLMenuItem extends MenuItem {

    ResourceBundle strings;

    Consumer<String> onReceiveResult;

    public LoadFromURLMenuItem(ResourceBundle strings) {
        setText(strings.getString("loadFromInternet"));
        this.strings = strings;
        setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("http://docs.neu.edu.tr/library/timetable.xlsx");
            dialog.getDialogPane().setPrefWidth(Integers.WIDE_DIALOG);
            dialog.setTitle(strings.getString("url"));
            dialog.setHeaderText(strings.getString("askForUrl"));
            dialog.show();
            dialog.setOnCloseRequest(ev -> {
                if (dialog.getResult() != null && onReceiveResult!=null) {
                    onReceiveResult.accept(dialog.getResult());
                }
            });
        });
    }

    public void setOnReceiveResult(Consumer<String> action){
        onReceiveResult = action;
    }





}
