package sample;

/*A string with a corresponding integer value as a rank this will
    typically be the index of a column or a row. This will be used for sorting.
    For instance, say you have a row in which times are stored in different cells.
    The lower the index the earlier the time.*/
public class RankedString {
    private String string;
    private int rank;

    public RankedString(String string, int rank) {
        this.string = string;
        this.rank = rank;
    }

    public String getString() {
        return string;
    }

    public int getRank() {
        return rank;
    }
}
