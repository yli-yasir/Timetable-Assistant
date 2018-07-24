package sample;

public enum SelectionMode {

    SELECT_COURSE("CHOOSE COURSE","Course: ", "Click a cell that contains a course..."),
    SELECT_DAY("CHOOSE DAY","Day: ","Click the cell that contains the day..."),
    SELECT_TIME("CHOOSE TIME","Time: ", "Click the cell that contains the time..."),
    SELECT_HALL("CHOOSE HALL","Hall: " ,"Click the cell that contains the hall...  ");

    private String title;
    private String prefix;
    private String description;

    SelectionMode(String title, String prefix, String description) {
        this.title = title;
        this.prefix = prefix;
        this.description = description;
    }

    String decription() {
        return description;
    }

     String title(){return title;}

     String prefix(){
        return prefix;
     }
}
