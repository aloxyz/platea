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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;


public class Container {
    private JSONObject config;
    private JSONObject labels;
    private String name;


    private String id;

    Container(JSONObject config, String jobName) {
        this.config = config;

        // Name generation
        byte[] array = new byte[8];
        new Random().nextBytes(array);
        String random = new String(array, StandardCharsets.UTF_8);

        this.name = config.getString("Image") + "_" + random;

        try {
            Database.getDatabase().insertContainer(this, jobName);
        }

        catch (InsertException e) {
            System.out.printf("Could not create container \"name\": %s%n", e.getMessage());
        }

        // Labels object setup
        this.labels = new JSONObject();
        this.labels.put("service", "platea");
        this.labels.put("job", jobName);
    }

    Container(String id) {
        this.id = id;

        try {
            Database.getDatabase().getContainer(id);
        }
        catch (GetException e) {
            System.out.printf("Could not initialize container \"name\": %s%n", e.getMessage());
            System.exit(1);
        }
    }

    public HttpResponse create() throws CreateContainerException {
        // Setting up parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("name", this.name);
        params.put("labels", this.labels.toString());

        HttpResponse createContainerResponse;
        String responseMessage;

        createContainerResponse =
                Docker.post("containers/create", "",
                        params,
                        BodyPublishers.ofString(this.config.toString()),
                        "application/json;charset=UTF-8"
                );

        JSONObject createContainerJsonObject = new JSONObject((createContainerResponse.body().toString()));

        if (createContainerResponse.statusCode() == 201) {
            this.id = createContainerJsonObject.get("Id").toString();
        } else {
            responseMessage = createContainerJsonObject.get("message").toString();
            throw new CreateContainerException("Could not create container: " + responseMessage);
        }

        return createContainerResponse;
    }

    public HttpResponse start() throws StartContainerException {
        HttpResponse startContainerResponse =
                Docker.post("containers", this.id + "/start",
                        Client.getClient().noParameters(),
                        Client.getClient().noBody(),
                        "application/json;charset=UTF-8"
                );

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
                Docker.post("containers", id + "/stop",
                        Client.getClient().noParameters(),
                        Client.getClient().noBody(),
                        "application/json;charset=UTF-8"
                );
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

        HttpResponse deleteContainerResponse = Docker.delete("containers", this.id, params);

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
                Docker.get("containers", id, params);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

}
