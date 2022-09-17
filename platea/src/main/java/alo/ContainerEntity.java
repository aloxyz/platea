package alo;

public class ContainerEntity {
    private int id;
    private Container container;
    private boolean is_running;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setContainer(Container c) {
        this.container = c;
    }

    public Container getContainer() {
        return this.container;
    }

    public boolean isRunning() {
        return this.is_running;
    }

    public void setRunning(boolean b) {
        this.is_running = b;
    }
}
