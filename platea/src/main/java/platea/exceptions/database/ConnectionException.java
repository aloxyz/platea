package platea.exceptions.database;

public class ConnectionException extends Exception {
    public ConnectionException() {
        super("Could not connect to database");
    }

    public ConnectionException(String message) {
        super(message);
    }
}
