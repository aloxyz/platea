package platea;

import org.json.JSONArray;
import org.json.JSONObject;
import platea.exceptions.CreateJobException;
import platea.exceptions.database.DeleteException;
import platea.exceptions.database.GetException;
import platea.exceptions.database.InsertException;
import platea.exceptions.database.UpdateException;
import platea.exceptions.docker.*;

import javax.xml.crypto.Data;
import java.net.http.HttpResponse;
import java.sql.Array;
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

            String[] dbArray = (String[]) rs.getArray("containers").getArray();
            this.containers = new ArrayList<>(Arrays.asList(dbArray));

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

            for (int i = 0; i < images.length(); i++) {
                JSONObject imageConfig = (JSONObject) images.get(i);
                String imageName = imageConfig.getString("name");

                // delete images and put response into imagesResponses
                imagesResponses.put(imageName, new Image(imageName).delete("true"));
            }

            responses.put("images", imagesResponses); //put imagesResponses into full responses Map

            return responses;

        } catch (DeleteImageException e) {
            throw new DeleteJobException("Could not purge job: " + e.getMessage());
        }
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
