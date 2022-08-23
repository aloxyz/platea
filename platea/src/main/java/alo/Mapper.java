package alo;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Mapper {
    ObjectMapper mapper = new ObjectMapper();

    public Instance InstanceFromFile(String path) {
        Instance i = new Instance();     
        try {
            Container c = new Container();
            ArrayList<LinkedHashMap<String,String>> tmp = mapper.readValue(Paths.get(path).toFile(),ArrayList.class);
            for (LinkedHashMap<String,String> e : tmp) {
                c.InitializeFromLHM(e);
                i.containers.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public Container ContainerFromFile(String path) {
        Container c = new Container();
        try {
            c = mapper.readValue(
                Paths.get(path).toFile(), 
                Container.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public Container ContainerFromURL(String url) {
        Container c = new Container();
        try {
            c = mapper.readValue(
                new URL(url), 
                Container.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public void ContainerToFile(Container c, String path) {
        try {
            mapper.writeValue(new File(path), c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
