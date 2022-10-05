package platea;

import org.json.JSONObject;
import platea.exceptions.CreateContainerException;
import platea.exceptions.DockerException;

import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.HashMap;


public class Container {
    private JSONObject config;
    private final JSONObject labels;
    private final String name;
    private final String id = "";

    Container(JSONObject config, String jobName) {
        this.config = config;

        this.name = config.getString("Image");

        // Labels object setup
        this.labels = new JSONObject();
        this.labels.put("service", "platea");
        this.labels.put("job", jobName);
    }

    public HttpResponse create() throws CreateContainerException {
        // Setting up parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("name", this.name);
        params.put("labels", this.labels.toString());

        HttpResponse createContainerResponse;
        String responseMessage;

        // id = Database.getDatabase().getContainer(this);

        //if (id.isEmpty()) {
            createContainerResponse =
                Docker.post("containers/create", "",
                        params,
                        BodyPublishers.ofString(this.config.toString()),
                        "application/json;charset=UTF-8"
                );

        JSONObject createContainerJsonObject = new JSONObject((createContainerResponse.body().toString()));

        if (createContainerResponse.statusCode() != 201) {
            responseMessage = createContainerJsonObject.get("message").toString();
            throw new CreateContainerException("Could not create container: " + responseMessage);
        }

        //this.id = createContainerJsonObject.get("Id").toString();
        //Database.getDatabase().insertContainer(this);

        //} else this.id = id;

        return createContainerResponse;
}

    public HttpResponse inspect() {
        HashMap<String, String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"instance\"]}");

        return
                Docker.get("containers", id, params);
    }

    public HttpResponse delete(String force) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("force", force);
        Database.getDatabase().deleteContainer(this);

        HttpResponse deleteContainerResponse = Docker.delete("containers", this.id, params);

        System.out.println(deleteContainerResponse.body().toString());
        return deleteContainerResponse;

    }

    public HttpResponse start() {
        HttpResponse tmp = null;
        try {
            if (!Database.getDatabase().getContainer(this).isEmpty()) {
                tmp =
                        Docker.post("containers", id + "/start",
                                Client.getClient().noParameters(),
                                Client.getClient().noBody(),
                                "application/json;charset=UTF-8"
                        );
            }
        } catch (Exception e) {
            System.out.println("Container does not exist or has not been initialized");
        }
        return tmp;
    }

    public HttpResponse stop() {
        return
                Docker.post("containers", id + "/stop",
                        Client.getClient().noParameters(),
                        Client.getClient().noBody(),
                        "application/json;charset=UTF-8"
                );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstance(Job job) {
        this.job = job;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public Job getInstance() {
        return job;
    }

    public JSONObject getConfig() {
        return config;
    }
}
