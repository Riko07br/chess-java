package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece {
    public Rook(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "R";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position positionToCheck = new Position(0, 0);

        //above checks
        positionToCheck.setValues(position.getRow() - 1, position.getColumn());
        while (getBoard().positionExists(positionToCheck) && !getBoard().theresAPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
            positionToCheck.setRow(positionToCheck.getRow() - 1);
        }
        if (getBoard().positionExists(positionToCheck) && isThereOpponentPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }

        //below checks
        positionToCheck.setValues(position.getRow() + 1, position.getColumn());
        while (getBoard().positionExists(positionToCheck) && !getBoard().theresAPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
            positionToCheck.setRow(positionToCheck.getRow() + 1);
        }
        if (getBoard().positionExists(positionToCheck) && isThereOpponentPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }

        //left checks
        positionToCheck.setValues(position.getRow(), position.getColumn() - 1);
        while (getBoard().positionExists(positionToCheck) && !getBoard().theresAPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
            positionToCheck.setColumn(positionToCheck.getColumn() - 1);
        }
        if (getBoard().positionExists(positionToCheck) && isThereOpponentPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }

        //right checks
        positionToCheck.setValues(position.getRow(), position.getColumn() + 1);
        while (getBoard().positionExists(positionToCheck) && !getBoard().theresAPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
            positionToCheck.setColumn(positionToCheck.getColumn() + 1);
        }
        if (getBoard().positionExists(positionToCheck) && isThereOpponentPiece(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }


        return moves;
    }
}
