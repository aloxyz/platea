package platea.exceptions;

public class DockerDeleteContainerException extends Exception {
    public DockerDeleteContainerException() {
        super("Could not delete container");
    }

    public DockerDeleteContainerException(String message) {
        super(message);
    }
}
