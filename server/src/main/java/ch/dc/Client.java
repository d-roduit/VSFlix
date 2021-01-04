package ch.dc;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <b>Client is the class that represents a client that connects to the server.</b>
 */
public class Client {

    /**
     * The client Socket.
     */
    private Socket socket;

    /**
     * The client ip address.
     */
    private String ip;

    /**
     * The exchanging port between the client and the server.
     */
    private int exchangingPort;

    /**
     * The http port used by the client.
     */
    private int httpPort;

    /**
     * The client shared files list.
     *
     * @see FileEntry
     */
    private List<FileEntry> files = new ArrayList<>();

    /**
     * Client constructor.
     *
     * @param socket
     *              The client socket.
     *
     * @see Client#extractIp(Socket)
     * @see Client#extractExchangingPort(Socket)
     */
    public Client(Socket socket){
        this.socket = socket;
        this.ip = extractIp(socket);
        this.exchangingPort = extractExchangingPort(socket);
        this.httpPort = -1;
    }

    /**
     * Returns the client ip address.
     *
     * @return the client ip address.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Returns the exchanging port between the client and server.
     *
     * @return the client and server exchanging port.
     */
    public int getExchangingPort() {
        return exchangingPort;
    }

    /**
     * Returns the http port used by the client.
     *
     * @return the client http port.
     */
    public int getHttpPort() {
        return httpPort;
    }

    /**
     * Returns the client Socket.
     *
     * @return the client Socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Returns the client shared files list.
     *
     * @return the client files list.
     */
    public List<FileEntry> getFiles() {
        return files;
    }

    /**
     * Sets the http port used by the client.
     *
     * @param httpPort
     *              The client http port.
     */
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    /**
     * Add a fileEntry to the client files list.
     *
     * @param fileEntry
     *              The fileEntry.
     *
     * @see FileEntry
     */
    public void addFileEntry(FileEntry fileEntry) {
        files.add(fileEntry);
    }

    /**
     * Remove a fileEntry from the client files list.
     *
     * @param fileEntry
     *              The fileEntry to remove.
     *
     * @see FileEntry
     */
    public void removeFileEntry(FileEntry fileEntry) {
        files.remove(fileEntry);
    }

    /**
     * Extract the client ip address from the its socket.
     *
     * @param socket
     *              The client socket.
     *
     * @return The client ip address.
     */
    private String extractIp(Socket socket) {
        InetAddress clientAddr = socket.getInetAddress();
        return clientAddr.getHostAddress();
    }

    /**
     * Extract the client and server exchanging port from its socket.
     *
     * @param socket
     *              The client socket.
     *
     * @return The client and server exchanging port.
     */
    private int extractExchangingPort(Socket socket) {
        return socket.getPort();
    }

    /**
     * Equals overriding method.
     * @param o
     *              An object.
     *
     * @return A boolean.
     */
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

    /**
     * HashCode overriding method.
     *
     * @return Objects.hash()
     */
    @Override
    public int hashCode() {
        return Objects.hash(socket, ip, exchangingPort, httpPort, files);
    }
}