package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece {
    public Bishop(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "B";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position positionToCheck = new Position(0, 0);

        int[] rowOffsets = {-1, -1, 1, 1};
        int[] columnOffsets = {-1, 1, -1, 1};

        for (int i = 0; i < 4; i++) {
            positionToCheck.setValues(position.getRow() + rowOffsets[i], position.getColumn() + columnOffsets[i]);
            while (getBoard().positionExists(positionToCheck) && !getBoard().theresAPiece(positionToCheck)) {
                moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
                positionToCheck.setValues(positionToCheck.getRow() + rowOffsets[i], positionToCheck.getColumn() + columnOffsets[i]);
            }
            if (getBoard().positionExists(positionToCheck) && isThereOpponentPiece(positionToCheck)) {
                moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
            }
        }
        return moves;
    }
}
