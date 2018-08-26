package com.yli.timetable_assistant.tasks;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class FetchOnlineFileTask extends CallbackTask<File> {
    private final File file;
    private final String url;


   public FetchOnlineFileTask(TaskCallbacks<File> callbacks, String url, File file) {
        super(callbacks);
        this.file = file;
        this.url= url;
    }

    @Override
    protected File call() throws Exception {
        FileUtils.copyURLToFile(new URL(url), file, 15000, 15000);
        return file;
    }
}
