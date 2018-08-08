package com.yli.timetable_assistant;

import javafx.concurrent.Task;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

abstract class ReadTask<V> extends Task<V> {

    final File file;

    ReadTask(TaskCallbacks<V> callbacks, File file){
        this.file=file;
        this.setOnRunning(e->callbacks.onLoading());
        this.setOnSucceeded(e->callbacks.onFinishedLoading(getValue()));
    }


    interface TaskCallbacks<V> {
        void onLoading();
        void onFinishedLoading(V result);
    }

}
