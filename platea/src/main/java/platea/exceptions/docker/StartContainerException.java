package platea.exceptions.docker;

public class StartContainerException extends Exception {
    public StartContainerException() {
        super("Could not start container");
    }

    public StartContainerException(String message) {
        super(message);
    }
}
