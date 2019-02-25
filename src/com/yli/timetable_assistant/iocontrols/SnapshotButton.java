package com.yli.timetable_assistant.iocontrols;

import com.yli.timetable_assistant.utils.FXUtils;
import com.yli.timetable_assistant.utils.IOUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;

public class SnapshotButton extends Button {

    public SnapshotButton(String text, Node snapshotNode){
        super(text);
        setOnAction(e-> snapshotNode.snapshot(
                result->{
                    FileChooser saveDialog = FXUtils.makeSaveDialog();
                    File file = saveDialog.showSaveDialog(this.getScene().getWindow());
                    if (file != null) IOUtils.saveImage(SwingFXUtils.fromFXImage(result.getImage(),null),file,FXUtils.getExtension(saveDialog));
                    return null;},
                null,null));
    }
}
