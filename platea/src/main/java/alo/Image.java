package alo;

import java.net.http.HttpResponse;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class Image implements IEntity{
    private String name;
    private Instance instance;
    private String uri;

    Image(String name, Instance instance, String uri) {
        this.name = name;
        this.instance = instance;
        this.uri = uri;
    }

    public HttpResponse create() throws Exception {
        // Create image from remote repository
        HashMap<String, String> labels = new HashMap<String, String>();
        labels.put("service", "platea");
        labels.put("instance", instance.getName());
        String jsonLabels = new JSONObject(labels).toJSONString();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("t", name);
        params.put("remote", uri);
        params.put("labels", jsonLabels);

        String name = Database.getDatabase().getImage(this);

        if (name.isEmpty()) {           
            Database.getDatabase().insertImage(this);
        }

        HttpResponse createImageResponse = 
            Docker.post("build", "",
            params,
            Client.getClient().noBody(),
            "application/x-www-form-urlencoded");

            System.out.println(createImageResponse.body().toString());

        return createImageResponse;
    }

    public HttpResponse inspect() {
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
