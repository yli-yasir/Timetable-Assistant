package com.yli.timetable_assistant.res;

import java.util.*;

public class MapResourceBundle<V> extends ResourceBundle {


     HashMap<String,V> map = new HashMap<>();

    @Override
    protected V handleGetObject(String key) {
        return map.get(key);
    }

    //Should return an enumeration of the keys in this bundle and it's parents.
    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keySet());
    }

    /*Returns keys found in only this bundle. Overridden because
   keySet() which is used in getKeys() internally uses this to get the keys of this bundle and it's parents,
   by invoking this on the parent to produce the whole set of keys.
   So when you extend this class, you only need to override handleKeySet() to return the keys of the child class.
   */
    @Override
    protected Set<String> handleKeySet() {
        return map.keySet();
    }
}
