package com.yli.timetable_assistant.res;

import java.util.ResourceBundle;

public class IntsBundle extends MapResourceBundle<Integer> {

    //todo consider making this a static class with constants

    public IntsBundle(){
        map.put("windowWidth",700);
        map.put("windowHeight",500);
        map.put("initialFontSize",16);
    }
    public int getInteger(String key){
       return map.get(key);
    }
}
