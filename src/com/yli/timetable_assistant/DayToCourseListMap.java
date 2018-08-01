package com.yli.timetable_assistant;

import java.util.ArrayList;
import java.util.TreeMap;

 class DayToCourseListMap extends TreeMap<RankedString, ArrayList<Course>> {

    DayToCourseListMap(){
//        super(new Comparator<RankedString>() {
//            @Override
//            public int compare(RankedString o1, RankedString o2) {
//                return Integer.compare(o1.getRank(),o2.getRank());
//            }
//        });
    }

}
