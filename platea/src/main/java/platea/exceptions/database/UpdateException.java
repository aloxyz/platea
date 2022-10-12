package platea.exceptions.database;

public class UpdateException extends Exception {
    public UpdateException() {
        super("Could not update record in database");
    }

    public UpdateException(String message) {
        super(message);
    }
}
