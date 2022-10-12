package platea.exceptions.docker;

public class StopContainerException extends Exception {
    public StopContainerException() {
        super("Could not stop container");
    }

    public StopContainerException(String message) {
        super(message);
    }
}
