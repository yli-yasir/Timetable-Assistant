package com.yli.timetable_assistant;

import java.awt.image.BufferedImage;

public class TableDrawTask extends ParamTask<BufferedImage,DayToCourseListMap>{

    private final int fontSize;

    TableDrawTask(TaskCallbacks<BufferedImage> callbacks, DayToCourseListMap param,int fontSize) {
        super(callbacks, param);
        this.fontSize = fontSize;
    }

    @Override
    protected BufferedImage call() throws Exception {
        return TableManager.drawTable(param,fontSize);
    }
}
