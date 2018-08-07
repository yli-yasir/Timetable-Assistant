package com.yli.timetable_assistant.res;

import java.util.*;

public class Resources extends ResourceBundle {

    public static final String path = "com.yli.timetable_assistant.res.Resources";

    private HashMap<String,Object> res = new HashMap<>();


    public Resources(){
        res.put("welcomeString","Please choose a file, then proceed to enter required information.");
        res.put("windowSize","Example window size:");
        res.put("WindowViewerBrowseButton","CHOOSE FILE");
        res.put("searchFieldPrompt","Course name");
        res.put("availableCoursesHeader","Available:");
        res.put("addedCoursesHeader","Added:");
        res.put("searchButton","Search cells");
        res.put("generateButton","Generate");
        res.put("insufficientInfoHeader","Insufficient information!");
        res.put("insufficientInfoBody","Please choose required information first!");
        res.put("notReadyToSearchHeader","Not ready to search yet!");
        res.put("notReadyToSearchBody","Have you added courses and specified output file name?");
        res.put("settingsHeader","Settings:");
        res.put("outputFileName","Output file name:");
        res.put("fontSize","Font size:");
        res.put("outputFileNameFieldPrompt","Student No");
        res.put("exampleWindowSize","Example window size:");

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
