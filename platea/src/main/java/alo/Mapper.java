package alo;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapper {
    ObjectMapper mapper = new ObjectMapper();

    public Instance InstanceFromFile(String path) {
        Instance i = new Instance();
        try {
            i = mapper.readValue(
                Paths.get(path).toFile(), 
                Instance.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public Instance InstanceFromURL(String url) {
        Instance i = new Instance();
        try {
            i = mapper.readValue(
                new URL(url), 
                Instance.class);
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
