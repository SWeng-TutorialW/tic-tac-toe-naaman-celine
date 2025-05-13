package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static int clientCounter = 0;
	private String[][] board = new String[3][3];
	private boolean isXTurn = true;
	private String currentPlayer;
	public SimpleServer(int port) {
		super(port);

	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client){
		String msgString = msg.toString();
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(msgString.startsWith("add client")){
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				client.sendToClient("client added successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if(msgString.startsWith("remove client")){
			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						clientCounter--;
						break;
					}
				}
			}
		} else if (msgString.startsWith("ip and socket changed")) {
			System.out.format("ip and socket changed successfully for client %s\n", client.getInetAddress().getHostAddress());
			if(clientCounter==0){
				clientCounter++;
				return ;
			} else if (clientCounter==1) {
				clientCounter++;
				try{
					SubscribersList.getFirst().getClient().sendToClient("Start1");
					SubscribersList.getLast().getClient().sendToClient("Start");
					try{
						wait(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					SubscribersList.getFirst().getClient().sendToClient("Your Turn");
					SubscribersList.getLast().getClient().sendToClient("Opponent Turn");
				}catch (IOException e) {
					e.printStackTrace();
				}
				System.out.format("the game started\n");
			}
		} else{
			System.out.format(msgString+"\n");
			int row = Integer.parseInt(String.valueOf(msgString.charAt(0))); // Convert the first character to an integer
			int col = Integer.parseInt(String.valueOf(msgString.charAt(1)));
			if(isXTurn){
				if(!client.equals(SubscribersList.getFirst().getClient())){
					return;
				}
				currentPlayer = "X";
			}else{
				if(!client.equals(SubscribersList.get(1).getClient())){
					return;
				}
				currentPlayer = "O";
			}
			if (board[row][col] != null) return;
			if(checkWinner("X")||checkWinner("O"))return;
			msgString = msgString.concat(currentPlayer);
			sendToAllClients(msgString);
			board[row][col] = currentPlayer;
			isXTurn=!isXTurn;

			if (checkWinner(currentPlayer)) {
				sendToAllClients("Player ".concat(currentPlayer ).concat( " Wins!").concat(msgString));
				return;
			}else if (isBoardFull()) {
				sendToAllClients("It's a Draw!".concat(msgString).concat(currentPlayer));
				return;
			}
			if(currentPlayer.equals("X")){
				sendToAllClients("O");
			}else{
				sendToAllClients("X");
			}
		}
	}
	private boolean checkWinner(String player) {
		// Check rows, columns, and diagonals for a win
		for (int i = 0; i < 3; i++) {
			// Check rows
			if (player.equals(board[i][0]) && player.equals(board[i][1]) && player.equals(board[i][2])) return true;
			// Check columns
			if (player.equals(board[0][i]) && player.equals(board[1][i]) && player.equals(board[2][i])) return true;
		}
		// Check diagonals
		if (player.equals(board[0][0]) && player.equals(board[1][1]) && player.equals(board[2][2])) return true;
		if (player.equals(board[0][2]) && player.equals(board[1][1]) && player.equals(board[2][0])) return true;

		return false;
	}

	private boolean isBoardFull() {
		// Check if all cells are filled
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == null) return false;
			}
		}
		return true;
	}
	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
