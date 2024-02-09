package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {
    public Knight(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "N";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position positionToCheck = new Position(0, 0);

        int[] rowOffsets = {-2, -2, 2, 2, -1, -1, 1, 1};
        int[] columnOffsets = {-1, 1, -1, 1, -2, 2, -2, 2};

        for (int i = 0; i < 8; i++) {
            positionToCheck.setValues(position.getRow() + rowOffsets[i], position.getColumn() + columnOffsets[i]);
            if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
                moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
            }
        }

        return moves;
    }

    private boolean canMove(Position position) {
        ChessPiece chessPiece = (ChessPiece) getBoard().piece(position);
        return chessPiece == null || chessPiece.getColor() != getColor();
    }
}
