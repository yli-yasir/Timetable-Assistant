package sample;

public enum SelectionStep {

    SELECT_COURSE("CHOOSE COURSE", "Click a cell that contains a course..."),
    SELECT_DAY("CHOOSE DAY", "Click the cell that contains the day..."),
    SELECT_TIME("CHOOSE TIME", "Click the cell that contains the time..."),
    SELECT_HALL("CHOOSE HALL", "Click the cell that contains the hall...  ");

    private String title;
    private String description;

    SelectionStep(String title, String description) {
        this.title = title;
        this.description = description;
    }

    String decription() {
        return description;
    }

     String title(){return title;}
}
