package ch.dc;

/**
 * The command enumeration.
 * Represents a command made by the client and received by the server.
 */
public enum Command {
    HTTPPORT("HTTPPORT"),
    GETALLFILES("GETALLFILES"),
    ADDFILE("ADDFILE"),
    UNSHAREFILE("UNSHAREFILE"),
    GETNBCONNECTEDCLIENTS("GETNBCONNECTEDCLIENTS"),
    DISCONNECT("DISCONNECT");

    public final String value;

    private Command(String value) {
        this.value = value;
    }
}