package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;

public class TicTacToeController {

    @FXML
    private GridPane boardGrid;

    private Button[][] buttons = new Button[3][3];

    private char mySymbol; // 'X' or 'O'
    private char currentTurn = 'X'; // Track whose turn it is
    private SimpleClient client;


    @FXML
    public void initialize() {
        System.out.println(" initialize() was called!");
        EventBus.getDefault().register(this);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button(" ");
                button.setPrefSize(100, 100);
                button.setDisable(true);
                int finalI = i;
                int finalJ = j;
                button.setOnAction(e -> sendMove(finalI, finalJ));
                boardGrid.add(button, j, i);
                buttons[i][j] = button;
            }
        }

        // Simulate receiving the player's symbol
        //AssignSymbolMessage assignMsg = new AssignSymbolMessage('X');
        //handleAssignSymbol(assignMsg);
    }

    public void setClient(SimpleClient client) {
        this.client = client;
    }

    @Subscribe
    public void onAssignSymbolMessage(AssignSymbolMessage msg) {
        handleAssignSymbol(msg);
    }

    @Subscribe
    public void onGameUpdateMessage(GameUpdateMessage msg) {
        handleGameUpdate(msg);
    }

    @Subscribe
    public void onGameOverMessage(GameOverMessage msg) {
        handleGameOver(msg);
    }



    private void sendMove(int row, int col) {
        try {
            MoveMessage move = new MoveMessage(row, col, mySymbol);
            client.sendToServer(move);
        } catch (Exception e) {
            e.printStackTrace();
            showInfo("Error", "Could not send move to server.");
        }
    }


    @Subscribe
    public void handleAssignSymbol(AssignSymbolMessage msg) {
        mySymbol = msg.getSymbol();
        showInfo("Symbol Assigned", "You are Player " + mySymbol);

        // Simulate initial game state
        char[][] emptyBoard = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                emptyBoard[i][j] = ' ';
            }
        }
        handleGameUpdate(new GameUpdateMessage(emptyBoard, mySymbol));
    }

    @Subscribe
    public void handleGameUpdate(GameUpdateMessage msg) {
        char[][] board = msg.getBoard();
        currentTurn = msg.getCurrentTurn();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(String.valueOf(board[i][j]));
                buttons[i][j].setDisable(board[i][j] != ' ' || currentTurn != mySymbol);
            }
        }
    }

    @Subscribe
    public void handleGameOver(GameOverMessage msg) {
        String message = (msg.getWinner() == 'D') ? "It's a draw!" : "Player " + msg.getWinner() + " wins!";
        showInfo("Game Over", message);
        disableAllButtons();
    }

    private char[][] getBoardState() {
        char[][] board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = buttons[i][j].getText().charAt(0);
            }
        }
        return board;
    }

    private void disableAllButtons() {
        for (Button[] row : buttons) {
            for (Button button : row) {
                button.setDisable(true);
            }
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
