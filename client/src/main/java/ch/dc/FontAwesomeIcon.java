package ch.dc;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;

public class FontAwesomeIcon extends Label {
    private SimpleObjectProperty<FontAwesome> icon = new SimpleObjectProperty<FontAwesome>();

    public FontAwesomeIcon() {
        this.getStyleClass().add("font-awesome-icon");
        this.getStylesheets().add(getClass().getResource("assets/css/fontAwesomeIcon.css").toExternalForm());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FontAwesomeIcon.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public FontAwesomeIcon(FontAwesome icon) {
        this();
        setIcon(icon);
    }

    public FontAwesome getIcon() {
        return iconProperty().getValue();
    }

    public void setIcon(FontAwesome icon) {
        iconProperty().setValue(icon);
        textProperty().set(String.valueOf(icon.getUnicode()));
    }

    public SimpleObjectProperty<FontAwesome> iconProperty() {
        return icon;
    }
}
