package com.yli.timetable_assistant.utils;

import com.yli.timetable_assistant.tasks.CallbackTask;
import com.yli.timetable_assistant.tasks.FetchOnlineFileTask;
import com.yli.timetable_assistant.tasks.TableReadTask;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

public class IOUtils {

    public static void startTask(Task task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    //Loads Url into the file, in a background thread with a task.
    public static void loadFromURL(String url, File tmpFile, CallbackTask.TaskCallbacks<File> callbacks) {
        if (tmpFile != null) {
            FetchOnlineFileTask fetchOnlineFileTask = new FetchOnlineFileTask(callbacks,
                    url, tmpFile);
            startTask(fetchOnlineFileTask);
        }
    }

    //Read the table in the background.
    public static void loadFromFile(File file,CallbackTask.TaskCallbacks<Workbook> callbacks) {
        TableReadTask tableReadTask = new TableReadTask(callbacks, file);
        startTask(tableReadTask);
    }

    public static File makeTempFile(ResourceBundle strings) {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("TA_TMP", null);
            tmpFile.deleteOnExit();
        } catch (IOException io) {
            FXUtils.showErrorAlert(strings, "badTempFileIOHeader", "badTempFileIOBody");
        }
        return tmpFile;
    }
}
