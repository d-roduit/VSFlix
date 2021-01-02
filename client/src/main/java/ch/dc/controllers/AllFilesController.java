package ch.dc.controllers;

import ch.dc.Router;
import ch.dc.models.ClientModel;
import javafx.fxml.FXML;

public class AllFilesController {

    private final static String viewName = "Layout/AllFiles";

    private final Router router = Router.getInstance();
    private final ClientModel clientModel = ClientModel.getInstance();

    @FXML
    public void initialize() {
        router.setCurrentRoute(viewName);
    }

}
