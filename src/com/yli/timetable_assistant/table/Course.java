package com.yli.timetable_assistant.table;

import java.util.Objects;

public class Course {
    private String name;
    private RankedString hall;
    private RankedString time;
    private RankedString day;

    public Course(String name,RankedString hall, RankedString time,RankedString day) {
        this.name=name;
        this.hall = hall;
        this.time = time;
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(name, course.name) &&
                Objects.equals(hall, course.hall) &&
                Objects.equals(time, course.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, hall, time);
    }

    public String getName() {
        return name;
    }

    public RankedString getHall() {
        return hall;
    }

    public RankedString getTime() {
        return time;
    }
    public RankedString getDay() {
        return day;
    }

    @Override
    public String toString(){
        return name + "\n\n" + hall + "\n\n" + time;
    }

}