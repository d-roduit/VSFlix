package ch.dc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * JavaFX Client App
 */
public class Client extends Application {

    public static ClientHttpServer clientHttpServer;
    public static Stage stage;
    public static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Client.stage = stage;

        stage.setOnCloseRequest(windowEvent -> clientHttpServer.stop());

        scene = new Scene(loadFXML("Connection"));

        Font fontawesomeFont = Font.loadFont(Client.class.getResourceAsStream("assets/fonts/Font-Awesome-5-Free-Solid-900.otf"), 13);
        if (fontawesomeFont == null) {
            // TODO: Log font loading error
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
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) { launch(); }

}