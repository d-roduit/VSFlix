import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client {

    private Socket socket;
    private String ip;
    private int exchangingPort;
    private int httpPort;
    private List<FileEntry> files = new ArrayList<>();

    public Client(Socket socket){
        this.socket = socket;
        this.ip = extractIp(socket);
        this.exchangingPort = extractExchangingPort(socket);
        this.httpPort = -1;
    }

    public String getIp() {
        return ip;
    }

    public int getExchangingPort() {
        return exchangingPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public Socket getSocket() {
        return socket;
    }

    public List<FileEntry> getFiles() {
        return files;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public void addFile(File file, FileType fileType) {
        FileEntry fileEntry = new FileEntry(file, fileType, getIp(), getHttpPort());
        files.add(fileEntry);
    }

    private String extractIp(Socket socket) {
        InetAddress clientAddr = socket.getInetAddress();
        return clientAddr.getHostAddress();
    }

    private int extractExchangingPort(Socket socket) {
        return socket.getPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return exchangingPort == client.exchangingPort &&
                httpPort == client.httpPort &&
                Objects.equals(socket, client.socket) &&
                Objects.equals(ip, client.ip) &&
                Objects.equals(files, client.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket, ip, exchangingPort, httpPort, files);
    }
}