package platea;

import org.json.JSONArray;
import org.json.JSONObject;
import platea.exceptions.CreateJobException;
import platea.exceptions.database.DeleteException;
import platea.exceptions.database.GetException;
import platea.exceptions.database.InsertException;
import platea.exceptions.database.UpdateException;
import platea.exceptions.docker.*;

import java.net.http.HttpResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Job {
    private JSONObject config;
    private final String name;
    private ArrayList<String> containers = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();

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
            // Get containers from database and store into this.containers
            ResultSet rs = Database.getDatabase().getJob(name);

            String[] dbContainers = (String[]) rs.getArray("containers").getArray();
            String[] dbImages = (String[]) rs.getArray("images").getArray();

            this.containers = new ArrayList<>(Arrays.asList(dbContainers));
            this.images = new ArrayList<>(Arrays.asList(dbImages));

        } catch (NullPointerException e) {
            throw new CreateJobException("Job does not exist");

        } catch (GetException | SQLException e) {
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

                Image image = new Image(imageConfig, this.name);
                this.images.add(image.getName());
            }

            // Create containers
            for (int i = 0; i < containers.length(); i++) {
                JSONObject containerConfig = (JSONObject) containers.get(i);

                Container container = new Container(containerConfig, this.name);
                this.containers.add(container.getId());
            }

            Database.getDatabase().updateJob(this);

        } catch (CreateImageException | CreateContainerException | UpdateException | InsertException e) {
            throw new CreateJobException(String.format("Could not build job \"name\": %s%n", e.getMessage()));
        }

        return null;
    }

    public Map<String, HttpResponse> delete() throws DeleteJobException {
        /* Delete containers from Docker Engine and then from database. Lastly, removes job from database */

        Map<String, HttpResponse> responses = new HashMap<>();

        try {
            for (String id : containers) {
                // delete container from Docker Engine and add response to responses
                responses.put(id, new Container(id).delete("true"));
                Database.getDatabase().deleteContainer(id);

            }

            Database.getDatabase().deleteJob(this.name);
            return responses;

        } catch (DeleteContainerException | DeleteException e) {
            System.out.println(e.getMessage());
            throw new DeleteJobException(e.getMessage());
        }
    }

    public Map<String, Map<String, HttpResponse>> purge() throws DeleteJobException {
        /* Identical to delete(), only difference is that purge() also deletes images from the Docker Engine */

        Map<String, Map<String, HttpResponse>> responses = new HashMap<>();
        Map<String, HttpResponse> imagesResponses = new HashMap<>();

        try {
            responses.put("containers", delete()); // delete containers and put response into responses

            JSONArray images = this.config.getJSONArray("images");

            for (String image : this.images) {
                // delete images and put response into imagesResponses
                imagesResponses.put(image, new Image(image).delete("true"));
            }

            responses.put("images", imagesResponses); //put imagesResponses into full responses Map

            return responses;

        } catch (DeleteImageException e) {
            throw new DeleteJobException("Could not purge job: " + e.getMessage());
        }
    }

    public Map<String, HttpResponse> start() throws StartContainerException, StopContainerException {
        Map<String, HttpResponse> responses = new HashMap<>();

        try {
            for (String id : containers) {
                // start container and add response to responses
                responses.put(id, new Container(id).start());
            }

            return responses;

        } catch (StartContainerException e) {
            stop();
            throw new StartContainerException("Could not start job: " + e.getMessage());
        }
    }

    public Map<String, HttpResponse> stop() throws StopContainerException {
        Map<String, HttpResponse> responses = new HashMap<>();

        for (String id : containers) {
            // stop container and add response to responses
            responses.put(id, new Container(id).stop());
        }

        return responses;
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

    public ArrayList<String> getImages() {
        return this.images;
    }

}
