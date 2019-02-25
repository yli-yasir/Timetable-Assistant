package com.yli.timetable_assistant.res;

public class StringsBundle extends MapResourceBundle<String> {
//todo clean unused strings!
    public StringsBundle(){
        map.put("appName","Timetable Assistant");
        map.put("welcomeString","Hi! Please choose a file...");
        map.put("windowSize","Example window size:");
        map.put("chooseFileButton","\uD83D\uDCC1 Choose File");
        map.put("searchFieldPrompt","\uD83D\uDCD6 Enter course name");
        map.put("availableCoursesHeader","\uD83D\uDCDC Available:");
        map.put("addedCoursesHeader","\uD83D\uDCDD Added:");
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
        map.put("incorrectInfoBody","Select a cell that is related to the course you selected!");
        map.put("chooseRemainingInfo","Please choose any remaining information by clicking its button");
        map.put("allDone","All done! You can search for and add courses now!");
        map.put("choose","Choose");
        map.put("instructionPrefix","Please click the cell that contains the ");
        map.put("course","Course");
        map.put("day","Day");
        map.put("time","Time");
        map.put("hall","Hall");
        map.put("loadFromInternet","From Internet");
        map.put("loadFromPC","From PC");
        map.put("genericAlertTitle","Woops!");
        map.put("badNetworkIOHeader","Connectivity problem");
        map.put("badNetworkIOBody","Something went wrong while trying to download the file from the URL!\n\nPlease check" +
                " the URL and your internet connection for any problems.\n\nIf the problem persists, " +
                "try to download the file manually.");
        map.put("badIOHeader","Problem opening file");
        map.put("badIOBody","Something went wrong while trying to open that file");
        map.put("courseNotChosenHeader","Course not chosen yet!");
        map.put("courseNotChosenBody","Please choose a course first!");
        map.put("fileNotChosenHeader","File not chosen yet!");
        map.put("fileNotChosenBody","Please choose a file first!");
        map.put("file","File");
        map.put("badTempFileIOHeader","Temp file IO Error");
        map.put("badTempFileIOBody","Something went wrong,please download the file manually then select it" +
                " from your PC instead.");
        map.put("url","URL:");
        map.put("askForUrl","Where do you want to download the file from?");
        map.put("rows","Rows");
        map.put("columns","Columns");
        map.put("saveImage","Save table image");

    }

}
