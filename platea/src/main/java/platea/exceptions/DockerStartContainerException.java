package platea.exceptions;

public class DockerStartContainerException extends Exception {
    public DockerStartContainerException() {
        super("Could not start container");
    }

    public DockerStartContainerException(String message) {
        super(message);
    }
}
