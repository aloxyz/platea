package platea.exceptions;

public class DockerGetException extends Exception {
    public DockerGetException() {
        super("Could not GET from Docker Engine");
    }

    public DockerGetException(String message) {
        super(message);
    }
}
