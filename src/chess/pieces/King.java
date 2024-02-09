package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

    private ChessMatch match;

    public King(Board board, Color color, ChessMatch match) {
        super(board, color);
        this.match = match;
    }

    @Override
    public String toString() {
        return "K";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position positionToCheck = new Position(0, 0);

        int[] rowOffsets = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] columnOffsets = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            positionToCheck.setValues(position.getRow() + rowOffsets[i], position.getColumn() + columnOffsets[i]);
            if (getBoard().positionExists(positionToCheck) && canMove(positionToCheck)) {
                moves[positionToCheck.getRow()][positionToCheck.getColumn()] = true;
            }
        }

        //castling
        if (getMoveCount() == 0 && !match.getCheck()) {
            for (int i = 0; i < 2; i++) {
                Position rookPosition = new Position(position.getRow(), position.getColumn() + (i == 0 ? 3 : -4));

                if (testRookCastling(rookPosition)) {
                    int loopStart = i == 0 ? 1 : -3;
                    int loopEnd = i == 0 ? 3 : 0;
                    boolean pieceFound = false;

                    for (int j = loopStart; j < loopEnd; j++) {
                        Position p = new Position(position.getRow(), position.getColumn() + j);
                        if (getBoard().theresAPiece(p)) {
                            pieceFound = true;
                            break;
                        }
                    }

                    if (!pieceFound) {
                        moves[position.getRow()][position.getColumn() + (i == 0 ? 2 : -2)] = true;
                    }
                }
            }

        }

        return moves;
    }

    private boolean canMove(Position position) {
        ChessPiece chessPiece = (ChessPiece) getBoard().piece(position);
        return chessPiece == null || chessPiece.getColor() != getColor();
    }

    private boolean testRookCastling(Position position) {
        ChessPiece chessPiece = (ChessPiece) getBoard().piece(position);
        return chessPiece instanceof Rook &&
                chessPiece.getMoveCount() == 0 &&
                chessPiece.getColor() == getColor();
    }

}
