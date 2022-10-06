package platea;

import org.json.JSONArray;
import org.json.JSONObject;

import platea.exceptions.CreateContainerException;
import platea.exceptions.CreateImageException;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Job {
    private JSONObject config;
    private final String name;
    private ArrayList<String> containers = new ArrayList<>();

    Job(JSONObject config, String name) {
        /* Create Job that does not yet exist in database */
        this.config = config;
        this.name = name;
    }

    Job(String name) {
        /* Initialize Job with name (Job exists in database) */
        this.name = name;
    }

    public HttpResponse build() {
        try {
            JSONArray images = this.config.getJSONArray("images");
            JSONArray containers = this.config.getJSONArray("containers");

            // Create images
            for (int i = 0; i < images.length(); i++) {
                JSONObject imageConfig = (JSONObject) images.get(i);

                Image image = new Image(imageConfig, this.name);
                image.create();
            }

            // Create containers
            for (int i = 0; i < containers.length(); i++) {
                JSONObject containerConfig = (JSONObject) containers.get(i);
                Container container = new Container(containerConfig, this.name);

                // Create container and push its id to this.containers
                JSONObject createContainerResponseJson = new JSONObject(container.create().body().toString());
                this.containers.add(createContainerResponseJson.getString("Id"));
            }

        } catch (CreateImageException | CreateContainerException e) {
            System.out.println("Could not build instance: " + e.getMessage());
        }

        return null;
    }

    public JSONObject getConfig() {
        return config;
    }

    public String getName() {
        return name;
    }

}
