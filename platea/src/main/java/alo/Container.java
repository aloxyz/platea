package alo;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.HashMap;


public class Container implements IEntity {
    private static String id;
    private static String name;
    private static Instance instance;
    private static JSONObject config;

    Container(String name, Instance instance, JSONObject config) throws Exception {
        Container.name = name;
        Container.instance = instance;
        Container.config = config;

        String id = Database.getDatabase().getContainer(this);
        if (id.isEmpty()) {
            JSONObject createContainerResponse = 
                JSONController.stringToJSONObject(create().body().toString());
    
            Container.id = createContainerResponse.get("Id").toString();

            Database.getDatabase().insertContainer(this);
        } else {
            Container.id = id;
        }


    }

    public static HttpResponse list(String status) throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("all", "true");
        if(instance.getName().isEmpty()) {
            params.put("filters", "{\"label\":[\"instance\"]}");
        }
        else {
            params.put("filters", String.format("{\"label\":[\"instance=%s\"]}", instance.getName()));
        }
        if(!status.isEmpty()) {
            params.put("filters", String.format("{\"status\": [\"%s\"]}", status));
        }


        return 
            Docker.get("containers", "", params);
    }

    private HttpResponse create() throws Exception {
        HashMap<String, String> labels = new HashMap<String, String>();
        labels.put("service", "platea");
        labels.put("instance", instance.getName());
        String jsonLabels = new JSONObject(labels).toJSONString();
        

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("labels", jsonLabels);
        
        String body =
            new ObjectMapper().writeValueAsString(config);

        return
            Docker.post("containers/create", "",
            params,
            BodyPublishers.ofString(body),
            "application/json;charset=UTF-8"
            );
    }

    public HttpResponse inspect() throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"instance\"]}");

        return 
            Docker.get("containers", id, params);
    }

    public HttpResponse delete(String force) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("force", force);

        Database.getDatabase().deleteContainer(this);
        
        return
            Docker.delete("containers", id, params);
    }

    public HttpResponse start() throws Exception {
        return
            Docker.post("containers", id + "/start",
            Client.getClient().noParameters(),
            Client.getClient().noBody(),
            "application/json;charset=UTF-8"
            );
    }

    public HttpResponse stop() throws Exception {
        return
            Docker.post("containers", id  + "/stop",
            Client.getClient().noParameters(),
            Client.getClient().noBody(),
            "application/json;charset=UTF-8"
            );
    }

    public static HttpResponse prune() throws Exception {
        return 
        Docker.post("/containers/prune", "",
        Client.getClient().noParameters(),
        Client.getClient().noBody(),
        "application/x-www-form-urlencoded");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        Container.id = id;
    }

    public void setName(String name) {
        Container.name = name;
    }

    public void setInstance(Instance instance) {
        Container.instance = instance;
    }

    public void setConfig(JSONObject config) {
        Container.config = config;
    }

    public Instance getInstance() {
        return instance;
    }

    public JSONObject getConfig() {
        return config;
    }
}
