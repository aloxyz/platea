package alo;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Containers {
    public static HttpResponse create(String name, String instance, JSONObject config) throws Exception {
        HashMap<String, String> labels = new HashMap<String, String>();
        labels.put("service", "platea");
        labels.put("instance", instance);
        String jsonLabels = new JSONObject(labels).toJSONString();
        

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("labels", jsonLabels);
        
        String body =
            new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(config);

        return
            Docker.post("containers/create", "",
            params,
            BodyPublishers.ofString(body),
            "application/json;charset=UTF-8"
            );
    }

    public static HttpResponse list() throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"service=platea\"]}");
        
        return 
            Docker.get("containers", "", params);
    }

    public static HttpResponse inspect(String id) throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"service=platea\"]}");

        return 
            Docker.get("containers", id, params);
    }

    public static HttpResponse delete(String id, String force) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("force", force);
        return
            Docker.delete("containers", id, params);
    }

    public static HttpResponse prune() throws Exception {
        return 
        Docker.post("/containers/prune", "",
        Client.getClient().noParameters(),
        Client.getClient().noBody(),
        "application/x-www-form-urlencoded");
    }

    public static HttpResponse start(String id) throws Exception {
        return
            Docker.post("containers", id + "/start",
            Client.getClient().noParameters(),
            Client.getClient().noBody(),
            "application/json;charset=UTF-8"
            );
    }

    public static HttpResponse stop(String id) throws Exception {
        return
            Docker.post("containers", id  + "/stop",
            Client.getClient().noParameters(),
            Client.getClient().noBody(),
            "application/json;charset=UTF-8"
            );
    }
}
