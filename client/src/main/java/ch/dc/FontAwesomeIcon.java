package ch.dc;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;

public class FontAwesomeIcon extends Label {
    private SimpleObjectProperty<FontAwesome> icon = new SimpleObjectProperty<FontAwesome>();
//    private ObjectProperty<FontAwesome> icon = new ObjectProperty<FontAwesome>() {
//        @Override
//        public void bind(ObservableValue<? extends FontAwesome> observableValue) {
//
//        }
//
//        @Override
//        public void unbind() {
//
//        }
//
//        @Override
//        public boolean isBound() {
//            return false;
//        }
//
//        @Override
//        public Object getBean() {
//            return null;
//        }
//
//        @Override
//        public String getName() {
//            return null;
//        }
//
//        @Override
//        public FontAwesome get() {
//            return null;
//        }
//
//        @Override
//        public void addListener(ChangeListener<? super FontAwesome> changeListener) {
//
//        }
//
//        @Override
//        public void removeListener(ChangeListener<? super FontAwesome> changeListener) {
//
//        }
//
//        @Override
//        public void addListener(InvalidationListener invalidationListener) {
//
//        }
//
//        @Override
//        public void removeListener(InvalidationListener invalidationListener) {
//
//        }
//
//        @Override
//        public void set(FontAwesome fontAwesome) {
//
//        }
//    };

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
