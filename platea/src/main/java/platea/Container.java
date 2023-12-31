package platea;

import org.json.JSONObject;
import platea.exceptions.database.GetException;
import platea.exceptions.database.InsertException;
import platea.exceptions.docker.CreateContainerException;
import platea.exceptions.docker.DeleteContainerException;
import platea.exceptions.docker.StartContainerException;
import platea.exceptions.docker.StopContainerException;

import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import static platea.Client.*;
import static platea.Database.getDatabase;


public class Container {
    private JSONObject config;
    private final JSONObject labels = new JSONObject();
    private String name;
    private String id;

    Container(JSONObject config, String jobName) throws CreateContainerException {
        /* Create Container that does not yet exist in database. This constructor calls create() */

        this.config = config;

        // Labels object setup
        this.labels.put("service", "platea");
        this.labels.put("job", jobName);

        // Name generation
        String uuid = UUID.randomUUID().toString();
        this.name = config.getString("Image") + "_" + uuid.toLowerCase().substring(0, 7);

        // Create container and insert into database
        try {
            JSONObject createContainerResponseJson = new JSONObject(create().body().toString());
            this.id = createContainerResponseJson.getString("Id");

            getDatabase().insertContainer(this, jobName);
        } catch (InsertException e) {
            System.out.printf("Could not create container \"name\": %s%n", e.getMessage());
        }
    }

    Container(String id) {
        /* Initialize Container with id (Container exists in database). Does not call create() */

        this.id = id;

        try {
            this.name = getDatabase().getContainer(id).getString("name");
        } catch (GetException | SQLException e) {
            System.out.printf("Could not initialize container \"name\": %s%n", e.getMessage());
            System.exit(1);
        }
    }

    private HttpResponse create() throws CreateContainerException {
        System.out.print("Creating container " + this.name + "... ");

        // Setting up parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("name", this.name);
        params.put("labels", this.labels.toString());

        HttpResponse createContainerResponse;
        String responseMessage;

        createContainerResponse =
                getClient().postResource("/containers/create",
                        params,
                        BodyPublishers.ofString(this.config.toString()),
                        "application/json;charset=UTF-8");

        JSONObject createContainerJsonObject = new JSONObject((createContainerResponse.body().toString()));

        if (createContainerResponse.statusCode() == 201) {
            this.id = createContainerJsonObject.get("Id").toString();
        } else {
            responseMessage = createContainerJsonObject.get("message").toString();
            throw new CreateContainerException("Could not create container: " + responseMessage);
        }

        System.out.println(ConsoleColors.GREEN_BRIGHT + "done" + ConsoleColors.RESET);
        return createContainerResponse;
    }

    public HttpResponse start() throws StartContainerException {
        HttpResponse startContainerResponse =
                getClient().postResource("/containers/" + this.id + "/start",
                        getClient().noParameters(),
                        getClient().noBody(),
                        "application/json;charset=UTF-8");

        if (startContainerResponse.statusCode() == 304) {
            throw new StartContainerException("Container already started");

        } else if (startContainerResponse.statusCode() != 204 && startContainerResponse.statusCode() != 304) {
            String message = new JSONObject(startContainerResponse.body().toString()).getString("message");
            throw new StartContainerException("Could not start container: " + message);
        }

        return startContainerResponse;
    }

    public HttpResponse stop() throws StopContainerException {
        HttpResponse stopContainerResponse =
                getClient().postResource("/containers/" + this.id + "/stop",
                        getClient().noParameters(),
                        getClient().noBody(),
                        "application/json;charset=UTF-8");

        if (stopContainerResponse.statusCode() == 304) {
            throw new StopContainerException("Container already stopped");

        } else if (stopContainerResponse.statusCode() != 204 && stopContainerResponse.statusCode() != 304) {
            String message = new JSONObject(stopContainerResponse.body().toString()).getString("message");
            throw new StopContainerException("Could not stop container: " + message);
        }

        return stopContainerResponse;
    }

    public HttpResponse delete(String force) throws DeleteContainerException {
        HashMap<String, String> params = new HashMap<>();
        params.put("force", force);

        HttpResponse deleteContainerResponse = getClient().deleteResource("/containers/" + this.id, params);

        if (deleteContainerResponse.statusCode() != 204) {
            String message = new JSONObject(deleteContainerResponse.body().toString()).getString("message");
            throw new DeleteContainerException("Could not delete container: " + message);
        }

        return deleteContainerResponse;
    }

    public HttpResponse inspect() {
        HashMap<String, String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"instance\"]}");

        return
                getClient().getResource("/containers/" + this.id + "/json", params);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

}
