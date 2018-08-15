package com.yli.timetable_assistant.res;

import java.util.*;

public class Strings extends MapResourceBundle<String> {

    public Strings(){
        map.put("appName","Timetable assistant");
        map.put("welcomeString","Please choose a file, then proceed to enter required information.");
        map.put("windowSize","Example window size:");
        map.put("browseButton","CHOOSE FILE");
        map.put("searchFieldPrompt","Course name");
        map.put("availableCoursesHeader","Available:");
        map.put("addedCoursesHeader","Added:");
        map.put("searchButton","Search cells");
        map.put("generateButton","Generate");
        map.put("insufficientInfoHeader","Insufficient information!");
        map.put("insufficientInfoBody","Please choose required information first!");
        map.put("notReadyToGenerateHeader","Not ready to generate yet!");
        map.put("notReadyToGenerateBody","Have you added courses and specified output file name?");
        map.put("settingsHeader","Settings:");
        map.put("outputFileName","Output file name:");
        map.put("fontSize","Font size:");
        map.put("outputFileNameFieldPrompt","Student No");
        map.put("exampleWindowSize","Example window size:");
        map.put("incorrectInfoHeader","Incorrect Information!");
        map.put("incorrectInfoBody","The cell you selected isn't in-line with the course cell you selected.");
        map.put("chooseRemainingInfo","Please choose the remaining information...");
        map.put("allDone","All done! You can search for and add courses now!");

        //Strings for generated table window, might be separated
        //from this class in the future.
        map.put("yourTimetable","Your timetable:");
        map.put("changeFont","Please change the font as suitable: ");
        map.put("saveImage","then click the table to save.");

    }

}
