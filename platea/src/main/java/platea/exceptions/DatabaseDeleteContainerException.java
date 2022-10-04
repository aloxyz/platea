package platea.exceptions;

public class DatabaseDeleteContainerException extends Exception {
    public DatabaseDeleteContainerException() {

        super("Could not delete container in database");
    }

    public DatabaseDeleteContainerException(String message)
    {
        super(message);
    }
}
