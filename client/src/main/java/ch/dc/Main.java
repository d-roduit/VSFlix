package ch.dc;

public class Main {
    public static void main(String[] args) {
        ClientHttpServer clientHttpServer = ClientHttpServer.getInstance();

        Client.clientHttpServer = clientHttpServer;
        Client.main(args);
    }
}
