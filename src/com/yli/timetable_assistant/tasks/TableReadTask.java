package com.yli.timetable_assistant.tasks;

import com.yli.timetable_assistant.table.TableUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;

public class TableReadTask extends CallbackTask<Workbook> {


    private final File file;


    public TableReadTask(TaskCallbacks<Workbook> callbacks,File file) {
        super(callbacks);
        this.file = file;
    }

    @Override
    protected Workbook call() throws Exception {
        return TableUtils.readTable(file);
    }



}
