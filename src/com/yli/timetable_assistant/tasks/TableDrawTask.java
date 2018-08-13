package com.yli.timetable_assistant.tasks;


import com.yli.timetable_assistant.table.DayToCourseListMap;
import com.yli.timetable_assistant.table.TableArtist;

import java.awt.image.BufferedImage;

public class TableDrawTask extends ParamTask<BufferedImage,DayToCourseListMap> {

    private final int fontSize;

    public TableDrawTask(TaskCallbacks<BufferedImage> callbacks, DayToCourseListMap param, int fontSize) {
        super(callbacks, param);
        this.fontSize = fontSize;
    }

    @Override
    protected BufferedImage call() {
        return TableArtist.drawTable(param,fontSize);
    }
}
