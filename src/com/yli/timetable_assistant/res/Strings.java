package com.yli.timetable_assistant.res;

import java.util.*;

public class Strings extends ResourceBundle {

    public static final String PATH = "com.yli.timetable_assistant.res.Strings";

    private HashMap<String,Object> res = new HashMap<>();


    public Strings(){
        res.put("welcomeString","Please choose a file, then proceed to enter required information.");
        res.put("windowSize","Example window size:");
        res.put("browseButton","CHOOSE FILE");
        res.put("searchFieldPrompt","Course name");
        res.put("availableCoursesHeader","Available:");
        res.put("addedCoursesHeader","Added:");
        res.put("searchButton","Search cells");
        res.put("generateButton","Generate");
        res.put("insufficientInfoHeader","Insufficient information!");
        res.put("insufficientInfoBody","Please choose required information first!");
        res.put("notReadyToGenerateHeader","Not ready to generate yet!");
        res.put("notReadyToGenerateBody","Have you added courses and specified output file name?");
        res.put("settingsHeader","Settings:");
        res.put("outputFileName","Output file name:");
        res.put("fontSize","Font size:");
        res.put("outputFileNameFieldPrompt","Student No");
        res.put("exampleWindowSize","Example window size:");
        res.put("incorrectInfoHeader","Incorrect Information!");
        res.put("incorrectInfoBody","The cell you selected isn't in-line with the course cell you selected.");
        res.put("chooseRemainingInfo","Please choose the remaining information...");
        res.put("allDone","All done! You can search for and add courses now!");
        //Strings for generated table window, might be separated
        //from this class in the future.
        res.put("changeFont","Please change the font as suitable: ");
        res.put("saveImage","then click the table to save.");


    }

    @Override
    protected Object handleGetObject(String key) {
        return res.getOrDefault(key,null);
    }

    //Should return an enumeration of the keys in this bundle and it's parents.
    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keySet());
    }

    /*Returns keys found in only this bundle. Overridden because
    keySet() which is used in getKeys() internally uses this to get the keys of this bundle and it's parents,
    then it's also invoked on the parent to produce the whole set of keys.
    So when you extend this class, you only need to override handleKeySet() to return the keys of the child class.
    */
    @Override
    protected Set<String> handleKeySet() {
        return res.keySet();
    }
}
