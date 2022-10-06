package platea;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import platea.exceptions.CreateContainerException;
import platea.exceptions.DeleteContainerException;
import platea.exceptions.StartContainerException;
import platea.exceptions.StopContainerException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tests {
    @Test
    public void json() throws IOException {
        JSONObject job = new JSONObject(Files.readString(Paths.get("/home/alo/Documenti/platea/sample.json")));
        String name = job.getString("name");

        JSONArray images = job.getJSONArray("images");
        for (int i = 0; i < images.length(); i++) {
            JSONObject image = (JSONObject) images.get(i);
            String imageName = image.getString("name");
            String endpoint = image.getString("endpoint");
            boolean source = image.getBoolean("source");
            boolean script = image.getBoolean("script");

            System.out.printf("name: %s\nendpoint: %s\nbuild from source: %s\nhas setup script: %s\n", imageName, endpoint, source, script);
        }

        JSONArray containers = job.getJSONArray("containers");
        for (int i = 0; i < containers.length(); i++) {
            JSONObject container = (JSONObject) containers.get(i);

            System.out.printf("container config: %s", container.toString(4));
        }
    }

    @Test
    public void job() throws Exception {
        JSONObject job = new JSONObject(Files.readString(Paths.get("/home/alo/dev/platea/sample.json")));
        String jobName = job.getString("name");

        JSONArray images = job.getJSONArray("images");
        JSONArray containers = job.getJSONArray("containers");

        for (int i = 0; i < images.length(); i++) {
            JSONObject imageConfig = (JSONObject) images.get(i);
            Image image = new Image(imageConfig, jobName);

            image.create();
        }

        for (int i = 0; i < containers.length(); i++) {
            JSONObject containerConfig = (JSONObject) containers.get(i);
            Container container = new Container(containerConfig, jobName);

            try {
                System.out.println("created " + container.create().body().toString());
                container.start();
                container.stop();
                container.delete("true");

            } catch (CreateContainerException |
                     StartContainerException |
                     DeleteContainerException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}

