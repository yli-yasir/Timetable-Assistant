package com.yli.timetable_assistant.choosefile;

import com.yli.timetable_assistant.res.Numbers;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

import java.util.ResourceBundle;
import java.util.function.Consumer;

final public class LoadFromURLMenuItem extends MenuItem {


    private Consumer<String> onReceiveResult;

     LoadFromURLMenuItem(ResourceBundle strings) {
        setText(strings.getString("loadFromInternet"));
        setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("http://docs.neu.edu.tr/library/timetable.xlsx");
            dialog.getDialogPane().setPrefWidth(Numbers.WIDE_DIALOG);
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
