package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class GameUpdateMessage implements Serializable {
    private char[][] board; // 'X', 'O', or ' ' (space)
    private char currentTurn; // whose turn it is now ('X' or 'O')

    public GameUpdateMessage(char[][] board, char currentTurn) {
        this.board = board;
        this.currentTurn = currentTurn;
    }

    public char[][] getBoard() {
        return board;
    }

    public char getCurrentTurn() {
        return currentTurn;
    }
}

