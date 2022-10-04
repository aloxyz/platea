package platea;

import org.json.simple.JSONObject;
import platea.exceptions.*;

import java.net.http.HttpResponse;
import java.util.HashMap;

public class Image {
    private final String name;
    private final Instance instance;
    private final String uri;

    Image(String name, Instance instance, String uri) {
        this.name = name;
        this.instance = instance;
        this.uri = uri;
    }

    public HttpResponse create() throws CreateImageException {
        /*Create image from remote repository*/
        try {
            Database db = Database.getDatabase();

            // Setting labels
            HashMap<String, String> labels = new HashMap<>();
            labels.put("service", "platea");
            labels.put("instance", instance.getName());
            String jsonLabels = new JSONObject(labels).toJSONString();

            // Setting parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("t", name);
            params.put("remote", uri);
            params.put("labels", jsonLabels);

            // If image record not in database, insert into database
            insert();

            // Build image
            HttpResponse createImageResponse = Docker.post("build", "",
                    params,
                    Client.getClient().noBody(),
                    "application/x-www-form-urlencoded");

            if (createImageResponse.statusCode() != 200) {
                db.delete(this.getClass(), "images");
                throw new CreateImageException();
            }

        } catch (DatabaseConnectionException | DatabaseDeleteException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public HttpResponse inspect() {
        return
                Docker.get("images", name, Client.getClient().noParameters());
    }

    public HttpResponse delete(String force) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("force", force);

        Database.getDatabase().delete(this.getClass(), "images");
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
