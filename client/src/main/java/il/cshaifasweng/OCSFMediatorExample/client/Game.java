package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class Game {
    private static Game game = null;

    @FXML
    private Button button00, button01, button02, button10, button11, button12, button20, button21, button22;

    @FXML
    private Label statusLabel;

    private String[][] board = new String[3][3];

    public Game() {
        EventBus.getDefault().register(this);
    }


    @FXML
    private void handleButtonClick(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();

        try {
            String msg = buttonId.substring(6, 8);
            SimpleClient.getClient().sendToServer(msg);
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void handleMessage(Object msg) {
        Platform.runLater(() -> {
            if (msg instanceof String) {
                String message = msg.toString();

                if (message.equals("Start1")) {
                    try {
                        SecondaryController.switchTogame();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (message.equals("Start")) {
                    try {
                        SecondaryController.switchTogame();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (message.equals("Opponent Turn")) {
                    setStatusLabel("Opponent Turn");
                    disableBoard();
                } else if (message.equals("Your Turn")) {
                    setStatusLabel("Your Turn");
                    enableBoard();
                } else if (message.equals("X") || message.equals("O")) {
                    if ("Your Turn".equals(statusLabel.getText())) {
                        setStatusLabel("Opponent Turn");
                        disableBoard();
                    } else {
                        setStatusLabel("Your Turn");
                        enableBoard();
                    }
                } else if (message.contains("Draw")) {
                    setStatusLabel(message);
                    disableBoard();
                } else if (message.startsWith("Player")) {
                    if ("Your Turn".equals(statusLabel.getText())) {
                        setStatusLabel("You Win!!");
                    } else {
                        setStatusLabel("You Lose");
                    }
                    disableBoard();
                }
            } else if (msg instanceof Object[]) { // Board update
                Object[] update = (Object[]) msg;
                int row = (int) update[0];
                int col = (int) update[1];
                String operation = (String) update[2];
                setGame(row, col, operation);
            }
        });
    }


    private void setStatusLabel(String statusText) {
        statusLabel.setText(statusText);
    }
    private void disableBoard() {
        button00.setDisable(true);
        button01.setDisable(true);
        button02.setDisable(true);
        button10.setDisable(true);
        button11.setDisable(true);
        button12.setDisable(true);
        button20.setDisable(true);
        button21.setDisable(true);
        button22.setDisable(true);
    }
    private void enableBoard() {
        button00.setDisable(false);
        button01.setDisable(false);
        button02.setDisable(false);
        button10.setDisable(false);
        button11.setDisable(false);
        button12.setDisable(false);
        button20.setDisable(false);
        button21.setDisable(false);
        button22.setDisable(false);
    }
    public static Game getGame() {
        if (game == null) {
            game = new Game();
        }
        return game;
    }

    private void setGame(int row, int col, String operation) {
        System.out.format("the rows is "+row+"the col is "+col+"\n");
        board[row][col] = operation;
        if (row == 0 && col == 0) button00.setText(operation);
        else if (row == 0 && col == 1) button01.setText(operation);
        else if (row == 0 && col == 2) button02.setText(operation);
        else if (row == 1 && col == 0) button10.setText(operation);
        else if (row == 1 && col == 1) button11.setText(operation);
        else if (row == 1 && col == 2) button12.setText(operation);
        else if (row == 2 && col == 0) button20.setText(operation);
        else if (row == 2 && col == 1) button21.setText(operation);
        else if (row == 2 && col == 2) button22.setText(operation);
    }
}
