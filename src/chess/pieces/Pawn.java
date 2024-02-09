package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {
    private ChessMatch match;

    public Pawn(Board board, Color color, ChessMatch match) {
        super(board, color);
        this.match = match;
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

        //enPassant-------------------
        if (position.getRow() == (getColor() == Color.WHITE ? 3 : 4)) {
            for (int i = 0; i < 2; i++) {
                int sideSign = i == 0 ? -1 : 1;

                Position sidePosition = new Position(position.getRow(), position.getColumn() + sideSign);
                if (getBoard().positionExists(sidePosition) &&
                        isThereOpponentPiece(sidePosition) &&
                        getBoard().piece(sidePosition) == match.getEnPassantVulnerable()) {
                    moves[sidePosition.getRow() + colorSign][sidePosition.getColumn()] = true;
                    break;
                }
            }
        }

        return moves;
    }
}
