package sample;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

public class DayToCourseListMap extends TreeMap<RankedString, ArrayList<Course>> {

    DayToCourseListMap(){
        super(new Comparator<RankedString>() {
            @Override
            public int compare(RankedString o1, RankedString o2) {
                if (o1.equals(o2)) return 0;

                if (o1.getRank() > o2.getRank()) return 1;
                if (o1.getRank() < o2.getRank())return -1;

                return o1.toString().compareTo(o2.toString());
            }
        });
    }

}
