package alo;

import java.net.http.HttpResponse;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class Image implements IEntity{
    private static String name;
    private static Instance instance;
    private static String uri;

    Image(String name, Instance instance, String uri) throws Exception {
        Image.name = name;
        Image.instance = instance;
        Image.uri = uri;

        String id = Database.getDatabase().getImage(this);

        if (id.isEmpty()) {
            create();            
            Database.getDatabase().insertImage(this);
        }
    }

    private HttpResponse create() throws Exception {
        // Create image from remote repository
        HashMap<String, String> labels = new HashMap<String, String>();
        labels.put("service", "platea");
        labels.put("instance", instance.getName());
        String jsonLabels = new JSONObject(labels).toJSONString();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("t", name);
        params.put("remote", uri);
        params.put("labels", jsonLabels);

        return
            Docker.post("build", "",
            params,
            Client.getClient().noBody(),
            "application/x-www-form-urlencoded");
    }

    public HttpResponse inspect() throws Exception {
        return
            Docker.get("images", name, Client.getClient().noParameters());
    }

    public HttpResponse delete(String force) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("force", force);

        Database.getDatabase().deleteImage(this);

        return
            Docker.delete("images", name, params);
    }

    public static HttpResponse list() throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("all", "true");
        if(instance.getName().isEmpty()) {
            params.put("filters", "{\"label\":[\"instance\"]}");
        }
        else {
            params.put("filters", String.format("{\"label\":[\"instance=%s\"]}", instance.getName()));
        }
        return
            Docker.get("images", "", params);
    }

    public static HttpResponse prune() throws Exception {
        return 
            Docker.post("/images/prune", "",
            Client.getClient().noParameters(),
            Client.getClient().noBody(),
            "application/x-www-form-urlencoded");
    }

    public Instance getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }


}
