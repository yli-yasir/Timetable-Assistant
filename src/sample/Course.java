package sample;

class Course {
    private String name;
    private RankedString hall;
    private RankedString time;

     Course(String name,RankedString hall, RankedString time) {
        this.name=name;
        this.hall = hall;
        this.time = time;
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
