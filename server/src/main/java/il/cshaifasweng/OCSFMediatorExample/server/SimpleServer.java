package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.MoveMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.GameUpdateMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.AssignSymbolMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.GameOverMessage;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	private ConnectionToClient playerX = null;
	private ConnectionToClient playerO = null;
	private char[][] board = new char[3][3];
	private char currentTurn = 'X'; // X always starts

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		try {
			if (msg instanceof String) {
				String msgString = msg.toString();

				if (msgString.startsWith("#warning")) {
					Warning warning = new Warning("Warning from server!");
					client.sendToClient(warning);
					System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
				}

				else if (msgString.startsWith("add client")) {
					SubscribedClient connection = new SubscribedClient(client);
					SubscribersList.add(connection);

					if (playerX == null) {
						playerX = client;
						client.sendToClient(new AssignSymbolMessage('X'));
						System.out.println("Assigned X to client: " + client.getInetAddress().getHostAddress());
					} else if (playerO == null) {
						playerO = client;
						client.sendToClient(new AssignSymbolMessage('O'));
						System.out.println("Assigned O to client: " + client.getInetAddress().getHostAddress());

						// Start the game when both players are ready
						initializeBoard();
						GameUpdateMessage update = new GameUpdateMessage(board, currentTurn);
						playerX.sendToClient(update);
						playerO.sendToClient(update);
						System.out.println("Game started.");
					} else {
						client.sendToClient(new Warning("Game already has two players!"));
					}
				}

				else if (msgString.startsWith("remove client")) {
					removeClient(client);
				}
			}
			else if (msg instanceof MoveMessage) {
				handleMove((MoveMessage) msg, client);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeClient(ConnectionToClient client) {
		if (!SubscribersList.isEmpty()) {
			SubscribersList.removeIf(subscribedClient -> subscribedClient.getClient().equals(client));
		}
		if (client.equals(playerX)) {
			playerX = null;
		} else if (client.equals(playerO)) {
			playerO = null;
		}
		System.out.println("Client disconnected: " + client.getInetAddress().getHostAddress());
	}

	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ======== Helper Methods for Tic-Tac-Toe ========

	private void initializeBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = ' ';
			}
		}
	}

	private void handleMove(MoveMessage move, ConnectionToClient client) throws IOException {
		int row = move.getRow();
		int col = move.getCol();
		System.out.println("Server received move: (" + move.getRow() + ", " + move.getCol() + ")");
		if ((currentTurn == 'X' && client != playerX) || (currentTurn == 'O' && client != playerO)) {
			client.sendToClient(new Warning("It's not your turn!"));
			return;
		}

		if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != ' ') {
			client.sendToClient(new Warning("Invalid move!"));
			return;
		}

		// Make the move
		board[row][col] = currentTurn;

		// Check for win or draw
		if (checkWin(currentTurn)) {
			sendGameOver(currentTurn);
		} else if (checkDraw()) {
			sendGameOver('D'); // Draw
		} else {
			// Continue game
			currentTurn = (currentTurn == 'X') ? 'O' : 'X';
			GameUpdateMessage update = new GameUpdateMessage(board, currentTurn);
			playerX.sendToClient(update);
			playerO.sendToClient(update);
		}
	}

	private boolean checkWin(char player) {
		// Rows
		for (int i = 0; i < 3; i++) {
			if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
		}
		// Columns
		for (int i = 0; i < 3; i++) {
			if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
		}
		// Diagonals
		if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true;
		if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true;

		return false;
	}

	private boolean checkDraw() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == ' ') return false;
			}
		}
		return true;
	}

	private void sendGameOver(char winner) throws IOException {
		GameOverMessage gameOver = new GameOverMessage(winner, board);
		playerX.sendToClient(gameOver);
		playerO.sendToClient(gameOver);

		// Reset board for new game (optional)
		initializeBoard();
		currentTurn = 'X';
		System.out.println("Game over. Winner: " + winner);
	}
}
