package platea.exceptions.database;

public class GetException extends Exception {
    public GetException() {
        super("Could not query container in database");
    }

    public GetException(String message) {
        super(message);
    }
}
