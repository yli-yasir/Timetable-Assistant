package com.yli.timetable_assistant;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

public class TableReadTask extends ReadTask<Workbook> {


    TableReadTask(TaskCallbacks<Workbook> callbacks, File file) {
        super(callbacks, file);
    }


    @Override
    protected Workbook call() {
        Workbook timetable = null;
        if (file != null) {
            try {
                timetable = WorkbookFactory.create(file);
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
        }
        return timetable;
    }
}
