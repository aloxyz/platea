package alo;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapper {
    ObjectMapper mapper = new ObjectMapper();

    public Instance InstanceFromFile(String path) {
        Instance c = new Instance();
        try {
            c = mapper.readValue(
                Paths.get(path).toFile(), 
                Instance.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public Instance InstanceFromURL(String url) {
        Instance c = new Instance();
        try {
            c = mapper.readValue(
                new URL(url), 
                Instance.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public void InstanceToFile(Instance c, String path) {
        try {
            mapper.writeValue(new File(path), c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
