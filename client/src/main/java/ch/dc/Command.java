package ch.dc;

public enum Command {
    HTTPPORT("HTTPPORT"),
    GETALLFILES("GETALLFILES"),
    ADDFILE("ADDFILE"),
    GETCONNECTEDCLIENTS("GETCONNECTEDCLIENTS"),
    DISCONNECT("DISCONNECT");

    public final String value;

    private Command(String value) {
        this.value = value;
    }
}