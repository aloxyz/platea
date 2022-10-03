package platea;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.HashMap;


public class Container implements IEntity {
    private String id;
    private String name;
    private Instance instance;
    private JSONObject config;

    Container(String name, Instance instance, JSONObject config) throws Exception {
        this.name = name;
        this.instance = instance;
        this.config = config;
    }

    public HttpResponse create() throws Exception {
        HashMap<String, String> labels = new HashMap<String, String>();
        labels.put("service", "platea");
        labels.put("instance", instance.getName());
        String jsonLabels = new JSONObject(labels).toJSONString();
        

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("labels", jsonLabels);
        
        String body = null;

        try {
        body =
            new ObjectMapper().writeValueAsString(config);
        }

        catch (JsonProcessingException e) {
            System.out.println("Could not process JSON");
        }

        String id = Database.getDatabase().getContainer(this);

        HttpResponse createContainerResponse = null;
        try {
            if (id.isEmpty()) {
                createContainerResponse =
                    Docker.post("containers/create", "",
                    params,
                    BodyPublishers.ofString(body),
                    "application/json;charset=UTF-8"
                    );

                JSONObject createContainerJsonObject = 
                    JSONController.stringToJSONObject(createContainerResponse.body().toString());

                System.out.println(createContainerJsonObject.get("message"));

                this.id = createContainerJsonObject.get("Id").toString();
    
                Database.getDatabase().insertContainer(this);

            } else {
                this.id = id;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Container " + name + " is not initialized");
        }

        return createContainerResponse;
    }

    public HttpResponse inspect() {
        HashMap<String,String> params = new HashMap<>();
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
            Docker.post("containers", id  + "/stop",
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
