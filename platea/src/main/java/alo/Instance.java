package alo;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Instance {
    private ArrayList<Container> containers;

    public void build() {
        try {
            File[] scripts = new File("scripts").listFiles();

            for (Container c : this.containers) {
                if (Arrays.asList(scripts).contains(c.name)) {
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
