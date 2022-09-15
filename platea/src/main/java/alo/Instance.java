package alo;
import java.util.ArrayList;

public class Instance extends Thread {
    private ArrayList<Container> containers;
    private String instanceFilePath;

    Instance(String instanceFilePath) {
        this.containers = new ArrayList<Container>();
        this.instanceFilePath = instanceFilePath;
    }
    
    public ArrayList<Container> getContainers() {
        return this.containers;
    }

    public Container getContainer(int index) {
        return this.containers.get(index);
    }

    public void addContainer(Container c) {
        this.containers.add(c);
    }

    public void removeContainer(Container c) {
        this.containers.remove(c);
    }

    public void buildContainers() throws Exception {
        for (Container c : this.containers) {
            if (c.hasScript()) {
                c.runScript();
            }

            System.out.println("Building container: "+c.getPlateaServiceName());
            c.build();
        }
    }

    public void startContainers() throws Exception {
        for (Container c : this.containers) {  
            System.out.println("Starting container: "+c.getPlateaServiceName());
            c.start();
        }
    }

    public void runContainers() throws Exception {
        for (Container c : this.containers) {
            System.out.println("Building and starting container: "+c.getPlateaServiceName());
            c.build();
            c.start();
        }
    }

    public void stopContainers() throws Exception {
        for (Container c : this.containers) {
            System.out.println("Stopping container: "+c.getPlateaServiceName());
            c.stop();
        }
    }

    public void deleteContainers() throws Exception {
        for (Container c : this.containers) {
            System.out.println("Deleting container: "+c.getPlateaServiceName());
            c.delete();
        }
    }
}


/*
    @Deprecated
    public void build() {
        try {
            File[] scripts = new File("scripts").listFiles();
            ArrayList<String> script_filenames = new ArrayList<String>();
            for (File f : scripts) {
                script_filenames.add(f.getPlateaServiceName());
            }

            for (Container c : this.containers) {
                if (script_filenames.contains(c.getPlateaServiceName() + ".sh")) {
                    System.out.println("Found script for "+c.getPlateaServiceName()+". Building...");
                    c.build();
                } else {
                    System.out.println("scripts/"+c.name+".sh : file not found");
                }
            }
                
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
*/