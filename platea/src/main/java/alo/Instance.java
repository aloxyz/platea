package alo;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Instance {
    public ArrayList<Container> containers;

    Instance() {
        this.containers = new ArrayList<Container>();
    }
    
    public ArrayList<Container> getContainers() {
        return this.containers;
    }

    public Container getContainer(int index) {
        return this.containers.get(index);
    }

    /*
    public void setContainers(ArrayList containers) {
        
    }
*/
    public void build() {
        try {
            File[] scripts = new File("scripts").listFiles();

            for (Container c : this.containers) {
                if (Arrays.asList(scripts).contains(c.getName())) {
                    c.build();
                } else {
                    System.out.println("scripts/"+c.name+".sh : file not found");
                }
            }
                
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
