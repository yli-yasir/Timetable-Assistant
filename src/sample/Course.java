package sample;

class Course {
    private String name;
    private String hall;
    private String time;

    public Course(String name,String hall, String time) {
        this.name=name;
        this.hall = hall;
        this.time = time;
    }


    public String getName() {
        return name;
    }

    public String getHall() {
        return hall;
    }

    public String getTime() {
        return time;
    }
}
