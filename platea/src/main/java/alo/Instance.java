package alo;
import java.io.File;
import java.util.ArrayList;

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
            ArrayList<String> script_filenames = new ArrayList<String>();
            for (File f : scripts) {
                script_filenames.add(f.getName());
            }

            for (Container c : this.containers) {
                if (script_filenames.contains(c.getName() + ".sh")) {
                    System.out.println("Found script for "+c.getName()+". Building...");
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
