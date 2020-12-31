package ch.dc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX Client App
 */
public class Client extends Application {

    public static Stage stage;
    public static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Client.stage = stage;
        scene = new Scene(loadFXML("Connection"));


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

    public static void main(String[] args) {
        launch();
    }

}