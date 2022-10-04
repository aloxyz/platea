package platea;

import org.json.simple.JSONObject;
import platea.exceptions.CreateImageException;
import platea.exceptions.DatabaseDeleteException;
import platea.exceptions.DatabaseGetException;
import platea.exceptions.DatabaseInsertImageException;

import java.net.http.HttpResponse;
import java.util.HashMap;

public class Image implements IEntity {
    private final String name;
    private final Instance instance;
    private final String uri;

    Image(String name, Instance instance, String uri) {
        this.name = name;
        this.instance = instance;
        this.uri = uri;
    }

    public HttpResponse create() throws CreateImageException {
        // Create image from remote repository
        Database db = Database.getDatabase();

        try {
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

            String name = db.get(this.getClass(), "images", "name");
            if (name.isEmpty()) db.insertImage(this);

            HttpResponse createImageResponse = Docker.post("build", "",
                    params,
                    Client.getClient().noBody(),
                    "application/x-www-form-urlencoded");

            if (createImageResponse.statusCode() != 200) {
                db.delete(this.getClass(), "images");
                throw new CreateImageException();
            }

        } catch (DatabaseInsertImageException | DatabaseDeleteException | DatabaseGetException e) {
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
