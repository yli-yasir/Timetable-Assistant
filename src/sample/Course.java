package sample;

class Course {
    private int dayColumn;
    private int dayRow;
    private int timeColumn;
    private int timeRow;
    private int hallRow;
    private int hallColumn;


    public Course(int dayColumn, int dayRow, int timeColumn, int timeRow, int hallRow, int hallColumn) {
        this.dayColumn = dayColumn;
        this.dayRow = dayRow;
        this.timeColumn = timeColumn;
        this.timeRow = timeRow;
        this.hallRow = hallRow;
        this.hallColumn = hallColumn;
    }
    public int getDayColumn() {
        return dayColumn;
    }

    public int getDayRow() {
        return dayRow;
    }
    public int getTimeColumn() {
        return timeColumn;
    }

    public int getTimeRow() {
        return timeRow;
    }

    public int getHallRow() {
        return hallRow;
    }

    public int getHallColumn() {
        return hallColumn;
    }



}
