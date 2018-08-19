package com.yli.timetable_assistant.tasks;


import com.yli.timetable_assistant.table.DayToCourseListMap;
import com.yli.timetable_assistant.table.TableArtist;

import java.awt.image.BufferedImage;

public class TableDrawTask extends CallbackTask<BufferedImage> {

    private final DayToCourseListMap dayToCourseListMap;
    private final int fontSize;

    public TableDrawTask(TaskCallbacks<BufferedImage> callbacks, DayToCourseListMap dayToCourseListMap, int fontSize) {
        super(callbacks);
        this.dayToCourseListMap= dayToCourseListMap;
        this.fontSize = fontSize;
    }

    @Override
    protected BufferedImage call() {
        return TableArtist.drawTable(dayToCourseListMap,
                TableArtist.DEFAULT_WIDTH,TableArtist.DEFAULT_HEIGHT
                ,fontSize);
    }
}
