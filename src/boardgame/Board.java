package boardgame;

public class Board {
    private int rows;
    private int columns;
    private Piece[][] pieces;

    public Board(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            throw new BoardException("Board creation error: board dimensions must be greater than 0");
        }
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];
    }

    //region Getters and Setters
    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
    //endregion

    public Piece piece(int row, int column) {
        if (!positionExists(row, column)) {
            throw new BoardException("Position is outside the board");
        }
        return pieces[row][column];
    }

    public Piece piece(Position position) {
        if (!positionExists(position)) {
            throw new BoardException("Position is outside the board");
        }
        return pieces[position.getRow()][position.getColumn()];
    }

    public void placePiece(Piece piece, Position position) {
        if (theresAPiece(position)) {
            throw new BoardException("There a piece at " + position);
        }
        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }

    public Piece removePiece(Position position){
        if(!theresAPiece(position)){
            return null;
        }
        Piece pieceToRemove = piece(position);
        pieceToRemove.position = null;
        pieces[position.getRow()][position.getColumn()] = null;
        return pieceToRemove;
    }

    private boolean positionExists(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    public boolean positionExists(Position position) {
        return positionExists(position.getRow(), position.getColumn());
    }

    public boolean theresAPiece(Position position) {
        if (!positionExists(position)) {
            throw new BoardException("Position is outside the board");
        }
        return piece(position) != null;
    }


}
