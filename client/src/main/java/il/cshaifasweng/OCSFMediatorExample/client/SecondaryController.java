package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    public static void switchTogame() throws IOException {
        try{
            App.setRoot("game");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}