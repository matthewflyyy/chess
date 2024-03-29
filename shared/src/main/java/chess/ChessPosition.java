package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int col;
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public boolean inBounds(){
        return (row < 9 && row > 0 && col < 9 && col > 0);
    }

    public ChessPosition copy(){
        return new ChessPosition(row, col);
    }

    public void updatePos(int rowChange, int colChange){
        row = row + rowChange;
        col = col + colChange;
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessPosition that=(ChessPosition) o;

        if (row != that.row) return false;
      return col == that.col;
    }

    @Override
    public int hashCode() {
        int result=row;
        result=31 * result + col;
        return result;
    }
}
