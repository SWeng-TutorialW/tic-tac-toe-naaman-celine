package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private SimpleClient client;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Connect to the server
        client = SimpleClient.getClient();
        client.openConnection();
        client.sendToServer("add client");

        // Load UI and set controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TicTacToe.fxml"));
        Parent root = loader.load();
        TicTacToeController controller = loader.getController();  // ✅ FIXED
        controller.setClient(client);

        // Set up JavaFX window
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tic-Tac-Toe Game");
        primaryStage.show();

        // Register to receive events
        EventBus.getDefault().register(this);
    }

    static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        scene.setRoot(loader.load());
    }

    @Override
    public void stop() throws Exception {
        EventBus.getDefault().unregister(this);
        if (client != null) {
            client.sendToServer("remove client");
            client.closeConnection();
        }
        super.stop();
    }

    @Subscribe
    public void onWarningEvent(WarningEvent event) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING,
                    String.format("Message: %s\nTimestamp: %s\n",
                            event.getWarning().getMessage(),
                            event.getWarning().getTime().toString())
            );
            alert.show();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
