package platea;

import org.json.JSONArray;
import org.json.JSONObject;
import platea.exceptions.CreateJobException;
import platea.exceptions.database.GetException;
import platea.exceptions.database.InsertException;
import platea.exceptions.database.UpdateException;
import platea.exceptions.docker.CreateContainerException;
import platea.exceptions.docker.CreateImageException;

import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Job {
    private JSONObject config;
    private final String name;
    private ArrayList<String> containers = new ArrayList<>();

    public Job(String name, JSONObject config) throws CreateJobException {
        /* Create Job that does not yet exist in database */
        this.config = config;
        this.name = name;
        build();
    }

    public Job(String name) throws CreateJobException {
        /* Initialize Job with name (Job exists in database) */
        this.name = name;

        try {
            Database.getDatabase().getJob(name);
        } catch (GetException e) {
            throw new CreateJobException(String.format("Could not initialize job \"name\": %s%n", e.getMessage()));
        }
    }

    private HttpResponse build() throws CreateJobException {
        try {
            JSONArray images = this.config.getJSONArray("images");
            JSONArray containers = this.config.getJSONArray("containers");

            Database.getDatabase().insertJob(this);

            // Create images
            for (int i = 0; i < images.length(); i++) {
                JSONObject imageConfig = (JSONObject) images.get(i);

                new Image(imageConfig, this.name);
            }

            // Create containers
            for (int i = 0; i < containers.length(); i++) {
                JSONObject containerConfig = (JSONObject) containers.get(i);

                Container container = new Container(containerConfig, this.name);
                this.containers.add(container.getId());
            }

            Database.getDatabase().updateJobContainerIDs(this);

        } catch (CreateImageException | CreateContainerException | UpdateException | InsertException e) {
            throw new CreateJobException(String.format("Could not build job \"name\": %s%n", e.getMessage()));
        }

        return null;
    }

    public JSONObject getConfig() {
        return this.config;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<String> getContainers() {
        return this.containers;
    }

}
