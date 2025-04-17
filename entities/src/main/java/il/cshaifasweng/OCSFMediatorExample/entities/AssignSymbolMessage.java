package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class AssignSymbolMessage implements Serializable {
    private char symbol; // 'X' or 'O'

    public AssignSymbolMessage(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}

