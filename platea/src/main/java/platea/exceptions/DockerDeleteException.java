package platea.exceptions;

public class DockerDeleteException extends Exception {
    public DockerDeleteException() {
        super("Could not DELETE from Docker Engine");
    }

    public DockerDeleteException(String message) {
        super(message);
    }
}
