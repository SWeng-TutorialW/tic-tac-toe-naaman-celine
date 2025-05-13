package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class GameOverMessage implements Serializable {
    private char winner; // 'X', 'O', or 'D' (D = Draw)
    private char[][] finalBoard;

    public GameOverMessage(char winner, char[][] finalBoard) {
        this.winner = winner;
        this.finalBoard = finalBoard;
    }

    public char getWinner() {
        return winner;
    }

    public char[][] getFinalBoard() {
        return finalBoard;
    }
}
