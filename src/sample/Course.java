package sample;

import java.util.Objects;

class Course {
    private String name;
    private RankedString hall;
    private RankedString time;

     Course(String name,RankedString hall, RankedString time) {
        this.name=name;
        this.hall = hall;
        this.time = time;
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

    String getName() {
        return name;
    }

     RankedString getHall() {
        return hall;
    }

     RankedString getTime() {
        return time;
    }
}
