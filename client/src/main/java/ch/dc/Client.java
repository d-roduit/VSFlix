package ch.dc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * JavaFX Client App
 */
public class Client extends Application {
    public static Logger logger;
    public static ClientHttpServer clientHttpServer;
    public static Stage stage;
    public static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Client.logger.info("Client starting...");

        Client.stage = stage;

        stage.setOnCloseRequest(windowEvent -> {
            Client.logger.info("Client closing...");
            ClientHttpServer.logger.info("Http server closing...");
            clientHttpServer.stop();
        });

        scene = new Scene(loadFXML("Connection"));

        Font fontawesomeFont = Font.loadFont(Client.class.getResourceAsStream("assets/fonts/Font-Awesome-5-Free-Solid-900.otf"), 13);
        if (fontawesomeFont == null) {
            Client.logger.severe("FontAwesome font (Font-Awesome-5-Free-Solid-900.otf) could not be loaded.");
        }

        stage.getIcons().add(new Image(getClass().getResourceAsStream("assets/images/v_small.png")));
        stage.setScene(scene);
        stage.setTitle("VSFlix");
        stage.setHeight(650);
        stage.setWidth(950);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        Client.logger.info("Load FXML file \"" + fxml +  ".fxml\" requested");
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) { launch(); }

}