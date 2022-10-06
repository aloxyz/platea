package platea.exceptions;

public class DockerCreateContainerException extends Exception {
    public DockerCreateContainerException() {
        super("Could not create container");
    }

    public DockerCreateContainerException(String message) {
        super(message);
    }
}
