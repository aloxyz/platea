package alo;

import java.net.http.HttpResponse;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class Images {
    public static HttpResponse buildRemote(String imageName, String instanceName, String uri) throws Exception {
        HashMap<String, String> labels = new HashMap<String, String>();
        labels.put("service", "platea");
        labels.put("instance", instanceName);
        String jsonLabels = new JSONObject(labels).toJSONString();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("t", imageName);
        params.put("remote", uri);
        params.put("labels", jsonLabels);

        return
            Docker.post("build", "",
            params,
            Client.getClient().noBody(),
            "application/x-www-form-urlencoded");
    }

    public static HttpResponse list(String instanceName) throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("all", "true");
        if(instanceName.isEmpty()) {
            params.put("filters", "{\"label\":[\"instance\"]}");
        }
        else {
            params.put("filters", String.format("{\"label\":[\"instance=%s\"]}", instanceName));
        }
        return
            Docker.get("images", "", params);
    }

    public static HttpResponse inspect(String id) throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"instance\"]}");

        return
            Docker.get("images", id, params);
    }    

    public static HttpResponse delete(String name, String force) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("force", force);
        return
            Docker.delete("images", name, params);
    }

    public static HttpResponse prune() throws Exception {
        return 
            Docker.post("/images/prune", "",
            Client.getClient().noParameters(),
            Client.getClient().noBody(),
            "application/x-www-form-urlencoded");
    }
}
