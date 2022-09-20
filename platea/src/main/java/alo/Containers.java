package alo;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Containers {
    public static HttpResponse create(String name, String configPath) throws Exception {
        JSONObject body = new JSONObject();
        body = JSONController.fileToJsonObject(configPath);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);

        String jsonBody =
            new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(body);

        return
            Docker.post("containers/create", "",
            params,
            BodyPublishers.ofString(jsonBody),
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
}
