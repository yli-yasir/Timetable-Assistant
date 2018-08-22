package com.yli.timetable_assistant.res;

import java.util.*;

public class StringsBundle extends MapResourceBundle<String> {

    public StringsBundle(){
        map.put("appName","Timetable Assistant");
        map.put("welcomeString","Please choose a file, then provide an example of a course with correct information.");
        map.put("windowSize","Example window size:");
        map.put("chooseFileButton","Choose File");
        map.put("searchFieldPrompt","Course name");
        map.put("availableCoursesHeader","Available:");
        map.put("addedCoursesHeader","Added:");
        map.put("searchButton","Search cells");
        map.put("generateButton","Generate");
        map.put("insufficientInfoHeader","Insufficient information!");
        map.put("insufficientInfoBody","Please choose required information first!");
        map.put("notReadyToGenerateHeader","Not ready to generate yet!");
        map.put("notReadyToGenerateBody","Have you added courses?");
        map.put("outputFileName","Output file name:");
        map.put("fontSize","Font size:");
        map.put("outputFileNameFieldPrompt","Student No");
        map.put("exampleWindowSize","Example window size:");
        map.put("incorrectInfoHeader","Probably incorrect Information!");
        map.put("incorrectInfoBody","The cell you selected isn't in-line with the course cell you selected.");
        map.put("chooseRemainingInfo","Please choose the remaining information... (Your info must correspond with the course name you choose!)");
        map.put("allDone","All done! You can search for and add courses now!");
        map.put("titlePrefix","Choose ");
        map.put("instructionPrefix","Please click the cell that contains the ");
        map.put("selectCourseModeName","Course");
        map.put("selectDayModeName","Day");
        map.put("selectTimeModeName","Time");
        map.put("selectHallModeName","Hall");
        map.put("loadFromInternet","From Internet");
        map.put("loadFromPC","From PC");
        map.put("genericAlertTitle","Something went wrong!");
        map.put("badNetworkIOHeader","Connectivity problem");
        map.put("badNetworkIOBody","Something went wrong while trying to download the file from the URL...\n\nPlease check" +
                " the URL and your internet connection for any problems.\n\nIf the problem persists, " +
                "try to download the file manually.");
        map.put("badIOHeader","Problem opening file");
        map.put("badIOBody","Something went wrong while trying to open that file");
        map.put("courseNotChosenHeader","Course not chosen yet!");
        map.put("courseNotChosenBody","Please choose a course first...");
        map.put("fileNotChosenHeader","File not chosen yet!");
        map.put("fileNotChosenBody","Please choose a file first...");
        map.put("filePrefix","File:");


        //StringsBundle for generated table window, might be separated
        //from this class in the future.
        map.put("yourTimetable","Your timetable:");
        map.put("changeFont","Please change the font as suitable: ");
        map.put("saveImage","then click the table to save.");
    }

}
