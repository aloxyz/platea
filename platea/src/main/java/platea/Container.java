package platea;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import platea.exceptions.DockerException;

import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.HashMap;


public class Container {
    private String id;
    private String name;
    private Instance instance;
    private JSONObject config;

    Container(String name, Instance instance, JSONObject config) {
        this.name = name;
        this.instance = instance;
        this.config = config;
    }

    public HttpResponse create() throws DockerException,  {
        HashMap<String, String> labels = new HashMap<>();
        labels.put("service", "platea");
        labels.put("instance", instance.getName());
        String jsonLabels = new JSONObject(labels).toJSONString();

        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("labels", jsonLabels);
        HttpResponse createContainerResponse = null;
        String responseMessage = null;

        try {
            String body = new ObjectMapper().writeValueAsString(config);
            String id = Database.getDatabase().getContainer(this);

            if (id.isEmpty()) {
                createContainerResponse =
                        Docker.post("containers/create", "",
                                params,
                                BodyPublishers.ofString(body),
                                "application/json;charset=UTF-8"
                        );

                JSONObject createContainerJsonObject =
                        JSONController.stringToJSONObject(createContainerResponse.body().toString());

                if (createContainerResponse.statusCode() != 201) {
                    responseMessage = createContainerJsonObject.get("message").toString();
                    throw new DockerException("Could not create container: " + responseMessage);
                }

                this.id = createContainerJsonObject.get("Id").toString();
                Database.getDatabase().insertContainer(this);

            } else this.id = id;

        } catch (JsonProcessingException e) {
            System.out.println("Could not process JSON");
            System.exit(0);
        }

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

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public Instance getInstance() {
        return instance;
    }

    public JSONObject getConfig() {
        return config;
    }
}
