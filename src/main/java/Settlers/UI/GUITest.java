package Settlers.UI;

import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GUITest {

    AnchorPane init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/assets/"));

        AnchorPane scene = null;
        try {
            scene = loader.load(FXGL.getAssetLoader().getStream("/assets/fxml/test.fxml"));
//            scene.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scene;
    }
}
