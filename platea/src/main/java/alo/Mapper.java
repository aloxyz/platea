package alo;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Mapper {

    public static Instance instanceFromFile(String name) throws Exception {
        Instance i = new Instance(name);     
        Container c;

        ArrayList<LinkedHashMap<String,Object>> tmp = 
            new ObjectMapper()
            .readValue(new File(name), ArrayList.class);

        for (LinkedHashMap<String,Object> e : tmp) {
            c = new Container();
            c.InitializeFromLHM(e);
            i.getContainers().add(c);
        }
        return i;
    }

    public static void instanceToFile(Instance i, String path) throws Exception{
        new ObjectMapper().writeValue(new File(path), i);
        
    }


    public static Container containerFromFile(String path) throws Exception {
        return new ObjectMapper().readValue(
            Paths.get(path).toFile(), 
            Container.class);
    }

    public static Container containerFromURL(String url) throws Exception {
        Container c = new Container();
        try {
            c = new ObjectMapper().readValue(
                new URL(url), 
                Container.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public static void containerToFile(Container c, String path) {
        try {
            new ObjectMapper().writeValue(new File(path), c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static String instanceToJSON(Instance i) throws Exception {
        return

        new ObjectMapper()
        .writer()
        .withDefaultPrettyPrinter()
        .writeValueAsString(i);
    }

    public static String InstanceEntityToJSON(InstanceEntity e) throws Exception {
        return

        new ObjectMapper()
        .writer()
        .withDefaultPrettyPrinter()
        .writeValueAsString(e);
    }

    public static String ContainerEntityToJSON(ContainerEntity c) throws Exception {
        return

        new ObjectMapper()
        .writer()
        .withDefaultPrettyPrinter()
        .writeValueAsString(c);
    }
}
