package platea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import platea.exceptions.CreateJobException;
import platea.exceptions.database.DeleteException;
import platea.exceptions.database.GetException;
import platea.exceptions.database.InsertException;
import platea.exceptions.database.UpdateException;
import platea.exceptions.docker.*;

import java.io.File;
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
    private File context;
    private HashMap<String, File> scripts; // Needed to build from remote repository

    public Job(String name, JSONObject config, File context) throws CreateJobException {
        /* Create Job that does not yet exist in database */
        this.config = config;
        this.name = name;
        this.context = context;

        build();
    }

    public Job(String name, JSONObject config, HashMap<String, File> scripts) throws CreateJobException {
        /* Create Job that does not yet exist in database, from remote repository */
        this.config = config;
        this.name = name;
        this.scripts = scripts;

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
        System.out.println("Creating job " + ConsoleColors.BLUE_BRIGHT + this.name + ConsoleColors.RESET + ": ");

        try {
            JSONArray images = this.config.getJSONArray("images");
            JSONArray containers = this.config.getJSONArray("containers");

            Database.getDatabase().insertJob(this);

            // todo build() returns Map<String, HttpResponse>;

            // Create images
            for (int i = 0; i < images.length(); i++) {
                JSONObject imageConfig = (JSONObject) images.get(i);
                Image image;

                // If script is needed, look for it in the context path
                if (!imageConfig.isNull("script")) {
                    File script;
                    String scriptName = imageConfig.getString("script");

                    //If building from remote repo
                    if (this.scripts != null) {
                        script = this.scripts.get(scriptName);

                    } else {
                        script = new File(context.getAbsolutePath() + "/" + scriptName);
                    }

                    if (!script.exists())
                        throw new CreateImageException("Script file \"" + scriptName + "\" does not exist");

                    if (!script.setExecutable(true))
                        throw new CreateImageException("Cannot make " + scriptName + " executable");


                    // If script file exists, pass it to constructor and build a new image
                    image = new Image(imageConfig, script, this.name);

                } else {
                    image = new Image(imageConfig, this.name);

                }
                this.images.add(image.getName());
            }

            // Create containers
            for (int i = 0; i < containers.length(); i++) {
                JSONObject containerConfig = (JSONObject) containers.get(i);

                Container container = new Container(containerConfig, this.name);
                this.containers.add(container.getId());
            }

            Database.getDatabase().updateJob(this);

        } catch (CreateImageException | CreateContainerException | UpdateException | InsertException |
                 JSONException e) {
            throw new CreateJobException(String.format(
                    "Could not build job "
                            + ConsoleColors.BLUE_BRIGHT
                            + this.name
                            + ConsoleColors.RESET
                            + ": %s%n", e.getMessage()));
        }

        System.out.println(ConsoleColors.GREEN_BRIGHT + "done" + ConsoleColors.RESET);
        return null;
    }

    public Map<String, HttpResponse> delete() throws DeleteJobException {
        /* Delete containers from Docker Engine and then from database. Lastly, removes job from database */

        Map<String, HttpResponse> responses = new HashMap<>();
        Container container = null;

        try {
            for (String id : containers) {
                // delete container from Docker Engine and add response to responses
                try {
                    container = new Container(id);
                    responses.put(id, container.delete("true"));
                    Database.getDatabase().deleteContainer(id);

                    System.out.println("Deleted container " + ConsoleColors.GREEN + container.getName() + ConsoleColors.RESET);

                } catch (DeleteContainerException e) {
                    System.out.println(ConsoleColors.YELLOW + container.getName() + ConsoleColors.RESET + ": " + e.getMessage());
                }


            }
            Database.getDatabase().deleteJob(this.name);
            System.out.println("Deleted job " + ConsoleColors.BLUE_BRIGHT + this.name + ConsoleColors.RESET);

        } catch (DeleteException e) {
            System.out.println(ConsoleColors.RED + container.getName() + ConsoleColors.RESET + ": " + e.getMessage());
        }

        return responses;
    }

    public Map<String, Map<String, HttpResponse>> purge() throws DeleteJobException {
        /* Identical to delete(), only difference is that purge() also deletes images from the Docker Engine */

        Map<String, Map<String, HttpResponse>> responses = new HashMap<>();
        Map<String, HttpResponse> imagesResponses = new HashMap<>();
        Image image = null;

        responses.put("containers", delete()); // delete containers and put response into responses

        for (String imageName : this.images) {
            // delete images and put response into imagesResponses
            try {
                image = new Image(imageName);
                imagesResponses.put(imageName, image.delete("true"));
                System.out.println("Deleted image " + ConsoleColors.GREEN + image.getName() + ConsoleColors.RESET);

            } catch (DeleteImageException e) {
                System.out.println(ConsoleColors.YELLOW + image.getName() + ConsoleColors.RESET + ": " + e.getMessage());
            }
        }

        responses.put("images", imagesResponses); //put imagesResponses into full responses Map

        return responses;
    }

    public Map<String, HttpResponse> start() throws StartContainerException, StopContainerException {
        Map<String, HttpResponse> responses = new HashMap<>();
        Container container = null;

        for (String id : containers) {
            // start container and add response to responses
            try {
                container = new Container(id);
                responses.put(id, container.start());
                System.out.println("Started container " + ConsoleColors.GREEN + container.getName() + ConsoleColors.RESET);

            } catch (StartContainerException e) {
                System.out.println(ConsoleColors.YELLOW + container.getName() + ConsoleColors.RESET + ": " + e.getMessage());
            }

        }

        return responses;
    }

    public Map<String, HttpResponse> stop() throws StopContainerException {
        Map<String, HttpResponse> responses = new HashMap<>();
        Container container = null;

        for (String id : containers) {
            // stop container and add response to responses
            try {
                container = new Container(id);
                responses.put(id, container.stop());
                System.out.println("Stopped container " + ConsoleColors.GREEN + container.getName() + ConsoleColors.RESET);

            } catch (StopContainerException e) {
                System.out.println(ConsoleColors.YELLOW + container.getName() + ConsoleColors.RESET + ": " + e.getMessage());
            }
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
