package platea.exceptions;

public class DockerStopContainerException extends Exception {
    public DockerStopContainerException() {
        super("Could not stop container");
    }

    public DockerStopContainerException(String message) {
        super(message);
    }
}
