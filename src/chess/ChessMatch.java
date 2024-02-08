package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Pawn;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {

    //region Private Vars
    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check = false;
    private boolean checkMate = false;

    private List<Piece> piecesOnBoard = new ArrayList<>();
    private List<Piece> piecesCaptured = new ArrayList<>();
    //endregion

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        setup();
    }

    //region Getters
    public ChessPiece[][] getPieces() {
        ChessPiece[][] chessPieces = new ChessPiece[board.getRows()][board.getColumns()];

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                chessPieces[i][j] = (ChessPiece) board.piece(i, j);
            }
        }

        return chessPieces;
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck() {
        return check;
    }

    public boolean getCheckMate() {
        return checkMate;
    }
    //endregion

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't out yourself in check");
        }

        check = testCheck(opponent(currentPlayer));

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else {
            nextTurn();
        }
        return (ChessPiece) capturedPiece;
    }

    //region Private methods
    private Piece makeMove(Position source, Position target) {
        ChessPiece piece = (ChessPiece) board.removePiece(source);
        piece.increaseMoveCount();

        Piece capturedPiece = board.removePiece(target);
        board.placePiece(piece, target);
        if (capturedPiece != null) {
            piecesOnBoard.remove(capturedPiece);
            piecesCaptured.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece piece = (ChessPiece) board.removePiece(target);
        piece.decreaseMoveCount();

        board.placePiece(piece, source);
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            piecesCaptured.remove(capturedPiece);
            piecesOnBoard.add(capturedPiece);
        }
    }

    private void nextTurn() {
        turn++;
        currentPlayer = currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private void validateSourcePosition(Position source) {
        if (!board.theresAPiece(source)) {
            throw new ChessException("No piece at source position");
        }
        if (currentPlayer != ((ChessPiece) board.piece(source)).getColor()) {
            throw new ChessException("Chosen piece is not yours");
        }
        if (!board.piece(source).isThereAnyPossibleMove()) {
            throw new ChessException("This Piece have no valid moves available");
        }
    }

    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("Chosen piece can't move to target position");
        }
    }

    private void placeNewPiece(char column, int row, ChessPiece chessPiece) {
        board.placePiece(chessPiece, new ChessPosition(column, row).toPosition());
        piecesOnBoard.add(chessPiece);
    }

    private Color opponent(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        for (Piece p : piecesOnBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).toList()) {
            if (p instanceof King)
                return (ChessPiece) p;
        }
        throw new IllegalStateException("No " + color + " king on board");
    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnBoard.
                stream().
                filter(x -> ((ChessPiece) x).getColor() == opponent(color)).
                toList();
        for (Piece p : opponentPieces) {
            if (p.possibleMoves()[kingPosition.getRow()][kingPosition.getColumn()])
                return true;
        }
        return false;
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color))
            return false;

        List<Piece> pieces = piecesOnBoard.
                stream().
                filter(x -> ((ChessPiece) x).getColor() == color).
                toList();

        for (Piece p : pieces) {
            boolean[][] possibleMoves = p.possibleMoves();
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (possibleMoves[i][j]) {
                        Position source = ((ChessPiece) p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);

                        boolean isInCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!isInCheck) {
                            return false;
                        }
                    }
                }

            }
        }

        return true;
    }

    private void setup() {
        for (int i = 0; i < 2; i++) {
            Color selectedColor = i == 1 ? Color.WHITE : Color.BLACK;
            int piecesRow = i == 1 ? 1 : 8;
            int pawnsRow = i == 1 ? 2 : 7;

            for (int j = 0; j < 8; j++) {
                placeNewPiece((char) ('a' + j), pawnsRow, new Pawn(board, selectedColor));
            }

            placeNewPiece('a', piecesRow, new Rook(board, selectedColor));
            placeNewPiece('c', piecesRow, new Bishop(board, selectedColor));
            placeNewPiece('e', piecesRow, new King(board, selectedColor));
            placeNewPiece('f', piecesRow, new Bishop(board, selectedColor));
            placeNewPiece('h', piecesRow, new Rook(board, selectedColor));
        }
    }
    //endregion

}
