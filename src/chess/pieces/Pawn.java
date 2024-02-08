package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {
    public Pawn(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "p";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position positionToCheck = new Position(0, 0);

        //if its white check forward (-1), else check forward for black (1)
        int colorSign = getColor() == Color.WHITE ? -1 : 1;

        positionToCheck.setValues(position.getRow() + colorSign, position.getColumn());
        if (getBoard().positionExists(positionToCheck) && !getBoard().theresAPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;

            //first move
            positionToCheck.setValues(position.getRow() + (2 * colorSign), position.getColumn());
            if (getBoard().positionExists(positionToCheck)
                    && !getBoard().theresAPiece(positionToCheck)
                    && getMoveCount() == 0) {

                moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
            }

        }
        //capture
        positionToCheck.setValues(position.getRow() + colorSign, position.getColumn() + 1);
        if (getBoard().positionExists(positionToCheck) && isThereOpponentPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }

        positionToCheck.setValues(position.getRow() + colorSign, position.getColumn() - 1);
        if (getBoard().positionExists(positionToCheck) && isThereOpponentPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }

        return moves;
    }
}
