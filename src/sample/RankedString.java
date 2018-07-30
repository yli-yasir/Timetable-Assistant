package sample;

import java.util.Objects;

/*A string with a corresponding integer value as a rank this will
    typically be the index of a column or a row. This will be used for sorting.
    For instance, say you have a row in which times are stored in different cells.
    The lower the index the earlier the time.*/
public class RankedString implements Comparable<RankedString> {
    private String string;
    private int rank;

     RankedString(String string, int rank) {
        this.string = string;
        this.rank = rank;
    }

     int getRank() {
        return rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RankedString that = (RankedString) o;
        return Objects.equals(string, that.string);
    }



    @Override
    public int hashCode() {
        return Objects.hash(string);
    }




    @Override
    public String toString() {
        return string;
    }


    @Override
    public int compareTo(RankedString o) {
        if (equals(o)) return 0;
        if (rank>o.getRank()) return 1;
        if (rank<o.getRank()) return -1;
        return 1;
    }
}
