package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {

    //region Private Vars
    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check = false;
    private boolean checkMate = false;

    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

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

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    public ChessPiece getPromoted() {
        return promoted;
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
            throw new ChessException("You can't put yourself in check");
        }

        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        //promotion----------------------
        promoted = null;
        if (movedPiece instanceof Pawn) {
            if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) ||
                    (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
                promoted = (ChessPiece) board.piece(target);
                promoted = replacePromoted("Q");
            }
        }

        check = testCheck(opponent(currentPlayer));

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else {
            nextTurn();
        }

        //enPassant-------------
        if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
            enPassantVulnerable = movedPiece;
        } else {
            enPassantVulnerable = null;
        }

        return (ChessPiece) capturedPiece;
    }

    public ChessPiece replacePromoted(String pieceType) {
        if (promoted == null) {
            throw new IllegalStateException("No piece to be promoted");
        }
        pieceType = pieceType.toUpperCase();
        if (!pieceType.equals("B") && !pieceType.equals("R") && !pieceType.equals("N") && !pieceType.equals("Q")) {
            return promoted;
        }

        Position pos = promoted.getChessPosition().toPosition();
        Piece pieceToRemove = board.removePiece(pos);
        piecesOnBoard.remove(pieceToRemove);

        ChessPiece newPiece = newChessPiece(pieceType, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnBoard.add(newPiece);
        return newPiece;
    }

    //region Private methods
    private ChessPiece newChessPiece(String type, Color color) {
        return switch (type) {
            case "B" -> new Bishop(board, color);
            case "N" -> new Knight(board, color);
            case "R" -> new Rook(board, color);
            default -> new Queen(board, color);
        };
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece piece = (ChessPiece) board.removePiece(source);
        piece.increaseMoveCount();

        Piece capturedPiece = board.removePiece(target);
        board.placePiece(piece, target);
        if (capturedPiece != null) {
            piecesOnBoard.remove(capturedPiece);
            piecesCaptured.add(capturedPiece);
        }

        //castling--------------
        if (piece instanceof King) {
            if ((target.getColumn() == source.getColumn() + 2) || (target.getColumn() == source.getColumn() - 2)) {
                boolean isKingSide = target.getColumn() == source.getColumn() + 2;

                Position sourceRook = new Position(source.getRow(), source.getColumn() + (isKingSide ? 3 : -4));
                Position targetRook = new Position(source.getRow(), source.getColumn() + (isKingSide ? 1 : -1));
                ChessPiece rook = (ChessPiece) board.removePiece(sourceRook);
                board.placePiece(rook, targetRook);
                rook.increaseMoveCount();
            }
        }

        //enPassant-------------
        if (piece instanceof Pawn && source.getColumn() != target.getColumn() && capturedPiece == null) {
            Position pawnPosition = new Position(target.getRow() - (piece.getColor() == Color.WHITE ? -1 : 1), target.getColumn());
            capturedPiece = board.removePiece(pawnPosition);
            piecesCaptured.add(capturedPiece);
            piecesOnBoard.remove(capturedPiece);
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

        //castling--------------
        if (piece instanceof King) {
            if ((target.getColumn() == source.getColumn() + 2) || (target.getColumn() == source.getColumn() - 2)) {
                boolean isKingSide = target.getColumn() == source.getColumn() + 2;

                Position sourceRook = new Position(source.getRow(), source.getColumn() + (isKingSide ? 3 : -4));
                Position targetRook = new Position(source.getRow(), source.getColumn() + (isKingSide ? 1 : -1));
                ChessPiece rook = (ChessPiece) board.removePiece(targetRook);
                board.placePiece(rook, sourceRook);
                rook.decreaseMoveCount();
            }
        }

        //enPassant-------------
        if (piece instanceof Pawn && source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
            ChessPiece pawn = (ChessPiece) board.removePiece(target);
            Position pawnPosition = new Position(piece.getColor() == Color.WHITE ? 3 : 4, target.getColumn());
            board.placePiece(pawn, pawnPosition);
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
                placeNewPiece((char) ('a' + j), pawnsRow, new Pawn(board, selectedColor, this));
            }

            placeNewPiece('a', piecesRow, new Rook(board, selectedColor));
            placeNewPiece('b', piecesRow, new Knight(board, selectedColor));
            placeNewPiece('c', piecesRow, new Bishop(board, selectedColor));
            placeNewPiece('d', piecesRow, new Queen(board, selectedColor));
            placeNewPiece('e', piecesRow, new King(board, selectedColor, this));
            placeNewPiece('f', piecesRow, new Bishop(board, selectedColor));
            placeNewPiece('g', piecesRow, new Knight(board, selectedColor));
            placeNewPiece('h', piecesRow, new Rook(board, selectedColor));
        }
    }
    //endregion

}
