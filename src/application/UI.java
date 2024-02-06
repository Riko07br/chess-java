package application;

import chess.ChessPiece;

public class UI {
    public static void printBoard(ChessPiece[][] chessPieces) {
        for (int i = 0; i < chessPieces.length; i++) {
            //print row id
            System.out.print((8 - i) + " ");

            for (int j = 0; j < chessPieces[i].length; j++) {
                printChessPiece(chessPieces[i][j]);
            }
            System.out.println();
        }
        //print col id
        System.out.println("  a b c d e f g h");
    }

    private static void printChessPiece(ChessPiece chessPiece) {
        if (chessPiece == null) {
            System.out.print("-");
        } else {
            System.out.print(chessPiece);
        }
        //Extra space
        System.out.print(" ");
    }
}
