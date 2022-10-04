package platea.exceptions;

public class DockerPostException extends Exception {
    public DockerPostException() {
        super("Could not POST to Docker Engine");
    }

    public DockerPostException(String message) {
        super(message);
    }
}
