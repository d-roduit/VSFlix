package ch.dc;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Router {

    private final static Router INSTANCE = new Router();

    private String previousRoute;
    private final StringProperty currentRoute = new SimpleStringProperty();
    private String nextRoutePartialView = null;


    private Router() {
        currentRoute.addListener((observable, oldValue, newValue) -> previousRoute = oldValue);
    }

    public static Router getInstance() { return INSTANCE; }

    public String[] getPreviousRoute() {
        return previousRoute.split("/");
    }

    public String[] getCurrentRoute() {
        return currentRoute.getValue().split("/");
    }

    public void setCurrentRoute(String currentRoute) {
        this.currentRoute.setValue(currentRoute);
    }

    public boolean hasPartialViewRequested() {
        return nextRoutePartialView != null;
    }

    public String getPartialViewRequested() {
        String tempNextRoutePartialView = nextRoutePartialView;
        nextRoutePartialView = null;

        return tempNextRoutePartialView;
    }

    public boolean isComposedRoute(String[] route) {
        return route.length > 1;
    }

    public void requestNextRoutePartialView(String partialView) {
        nextRoutePartialView = partialView;
    }

}
