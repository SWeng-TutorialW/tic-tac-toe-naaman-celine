package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.io.IOException;
import javafx.scene.control.Label;

public class PrimaryController {
	@FXML
	private Label label;
	@FXML
	private TextField ipField;

	@FXML
	private TextField socketField;

	@FXML
	void initialize(){

		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	private void handleSubmit() {
		String ip = ipField.getText();
		String socketText = socketField.getText();

		if (ip.isEmpty() || socketText.isEmpty()) {
			return;
		}
		int socket = Integer.parseInt(socketText);
		// Assign values to SimpleClient
		if(socketField.getText().equals("3000")) {
			SimpleClient.ip = ip;
			SimpleClient.port = socket;
			try {
				SimpleClient.getClient().sendToServer("ip and socket changed");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				App.setRoot("secondary");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			ipField.setText("");
			socketField.setText("");
			label.setText("Please try again with port that fits the server port");
		}
	}
}
