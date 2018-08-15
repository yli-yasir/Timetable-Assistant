package com.yli.timetable_assistant.res;

import java.util.ResourceBundle;

public class Integers extends MapResourceBundle<Integer> {

    public Integers(){
        map.put("windowWidth",700);
        map.put("windowHeight",500);
    }
    public int getInteger(String key){
       return map.get(key);
    }
}
