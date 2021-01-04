package ch.dc.models;

public class ClientHttpServerModel {

    private final static ClientHttpServerModel INSTANCE = new ClientHttpServerModel();


    private int port;
    private String contextPath;

    private ClientHttpServerModel() {}

    public static ClientHttpServerModel getInstance() { return INSTANCE; }

    public String getIp() { return "127.0.0.1"; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getContextPath() {
        return contextPath;
    }
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
