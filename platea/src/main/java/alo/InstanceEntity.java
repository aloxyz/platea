package alo;

import java.util.ArrayList;

public class InstanceEntity {
    private int id;
    private Instance instance;
    private boolean is_built;
    private ArrayList<ContainerEntity> containers;

    InstanceEntity() {
        containers = new ArrayList<ContainerEntity>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setInstance(Instance i) {
        this.instance = i;
    }

    public Instance getInstance() {
        return this.instance;
    }


    public boolean isRunning() {
        return this.is_built;
    }

    public void setRunning(boolean b) {
        this.is_built = b;
    }

    public ArrayList<ContainerEntity> getContainers() {
        return this.containers;
    }

    public void setContainersFromInstance() {
        ContainerEntity entity;
        
        for (Container c : this.instance.getContainers()) {
            entity = new ContainerEntity();
            entity.setContainer(c);
            this.getContainers().add(entity);
        }
    }
}
