package com.yli.timetable_assistant.res;

import java.util.*;

public class Resources extends ResourceBundle {

    private HashMap<String,Object> res = new HashMap<>();



    @Override
    protected Object handleGetObject(String key) {
        return res.getOrDefault(key,null);
    }

    //Should return an enumeration of the keys in the bundle and it's parents.
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
