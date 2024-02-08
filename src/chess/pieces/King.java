package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {
    public King(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "K";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position positionToCheck = new Position(0, 0);

        //above check
        positionToCheck.setValues(position.getRow() - 1, position.getColumn());
        if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }
        //above-left check
        positionToCheck.setValues(position.getRow() - 1, position.getColumn() - 1);
        if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }
        //left check
        positionToCheck.setValues(position.getRow(), position.getColumn() - 1);
        if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }
        //left-bellow check
        positionToCheck.setValues(position.getRow() + 1, position.getColumn() - 1);
        if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }
        //bellow check
        positionToCheck.setValues(position.getRow() + 1, position.getColumn());
        if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }
        //bellow-right check
        positionToCheck.setValues(position.getRow() + 1, position.getColumn() + 1);
        if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }
        //right check
        positionToCheck.setValues(position.getRow(), position.getColumn() + 1);
        if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }
        //right-above check
        positionToCheck.setValues(position.getRow() - 1, position.getColumn() + 1);
        if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
            moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
        }
        return moves;
    }

    private boolean canMove(Position position) {
        ChessPiece chessPiece = (ChessPiece) getBoard().piece(position);
        return chessPiece == null || chessPiece.getColor() != getColor();
    }

}
