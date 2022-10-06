package platea;

import org.json.JSONArray;
import org.json.JSONObject;

import platea.exceptions.CreateJobException;
import platea.exceptions.database.GetException;
import platea.exceptions.docker.CreateContainerException;
import platea.exceptions.docker.CreateImageException;
import platea.exceptions.database.InsertException;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Job {
    private JSONObject config;
    private final String name;
    private ArrayList<String> containers = new ArrayList<>();

    Job(String name, JSONObject config) throws CreateJobException {
        /* Create Job that does not yet exist in database */
        this.config = config;
        this.name = name;

        try {
            Database.getDatabase().insertJob(this);
        }

        catch (InsertException e) {
            throw new CreateJobException(String.format("Could not create job \"name\": %s%n", e.getMessage()));
        }
    }

    Job(String name) {
        /* Initialize Job with name (Job exists in database) */
        this.name = name;

        try {
            Database.getDatabase().getJob(name);
        }
        catch (GetException e) {
            System.out.printf("Could not initialize job \"name\": %s%n", e.getMessage());
            System.exit(1);
        }
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
