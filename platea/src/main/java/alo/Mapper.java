package alo;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Mapper {
    ObjectMapper mapper = new ObjectMapper();

    public Instance instanceFromFile(String name) throws Exception {
        Instance i = new Instance(name);     
        Container c;

        ArrayList<LinkedHashMap<String,Object>> tmp = 
            mapper.readValue(new File(name), ArrayList.class);

        for (LinkedHashMap<String,Object> e : tmp) {
            c = new Container();
            c.InitializeFromLHM(e);
            i.getContainers().add(c);
        }
        return i;
    }

    public void instanceToFile(Instance i, String path) throws Exception{
        mapper.writeValue(new File(path), i);
        
    }


    public Container containerFromFile(String path) throws Exception {
        return mapper.readValue(
            Paths.get(path).toFile(), 
            Container.class);
    }

    public Container containerFromURL(String url) throws Exception {
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

    public void containerToFile(Container c, String path) {
        try {
            mapper.writeValue(new File(path), c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
