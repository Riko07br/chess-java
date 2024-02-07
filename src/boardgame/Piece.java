package boardgame;

public abstract class Piece {
    protected Position position;
    private Board board;

    public Piece(Board board) {
        this.board = board;
    }

    protected Board getBoard() {
        return board;
    }

    public abstract boolean[][] possibleMoves();

    public boolean possibleMove(Position position) {
        return possibleMoves()[position.getRow()][position.getColumn()];
    }

    public boolean isThereAnyPossibleMove() {
        boolean[][] validMoves = possibleMoves();

        for (boolean[] validMove : validMoves) {
            for (boolean b : validMove) {
                if (b)
                    return true;
            }
        }

        return false;
    }
}
