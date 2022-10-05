package platea;

import org.json.JSONObject;
import platea.exceptions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Image {
    private final String name;
    private final String endpoint;
    private final boolean source;
    private final boolean script;
    private final JSONObject labels;

    Image(JSONObject config, String jobName) {
        this.name = config.getString("name");
        this.endpoint = config.getString("endpoint");
        this.source = config.getBoolean("source");
        this.script = config.getBoolean("script");

        // Labels object setup
        this.labels = new JSONObject();
        this.labels.put("service", "platea");
        this.labels.put("job", jobName);
    }

    public HttpResponse create() throws CreateImageException {
        HttpResponse createImageResponse;

        if (this.source) {
            createImageResponse = build();
        }
        else {
            createImageResponse = pull();
        }

        if (createImageResponse.statusCode() != 200) {
            JSONObject response = new JSONObject(createImageResponse.body().toString());
            System.out.println("Error while creating image: " + response.getString("message"));
            System.exit(1);
            throw new CreateImageException();
        }

        return createImageResponse;
    }

    public HttpResponse build() {
        /*Build image from source*/

        HttpResponse createImageResponse = null;
        Config config = Config.getConfig();
        String tmpPath = config.getTmpPath();
        String scriptsPath = config.scriptsPath();

        try {
            String trimmedName = this.name.substring(
                    this.name.lastIndexOf("/") + 1);

            // Pull image source from repo
            FileIO.bash(String.format("git clone %s %s", this.endpoint, tmpPath + trimmedName));

            // Make tarball from source
            File tar = FileIO.tar(tmpPath + trimmedName, tmpPath, trimmedName);
            Path tarPath = Paths.get(tar.getAbsolutePath());

            if (this.script) {
                FileIO.bash(scriptsPath + this.name);
            }

            // Setting parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("t", trimmedName);
            params.put("labels", this.labels.toString());

            createImageResponse = Docker.post("build", "",
                    params,
                    HttpRequest.BodyPublishers.ofFile(tarPath),
                    "application/x-tar");

            FileIO.cleanup();

        } catch (FileNotFoundException e) {
            System.out.println("Tarball was not found");
        }

        return createImageResponse;
    }

    public HttpResponse build(String endpoint) {
        /*Build image from remote repository*/
        HttpResponse createImageResponse;
        // Setting parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("remote", endpoint);
        params.put("labels", this.labels.toString());

        // Build image
        createImageResponse = Docker.post("build", "",
                params,
                Client.getClient().noBody(),
                "application/x-www-form-urlencoded");

        return createImageResponse;
    }

    public HttpResponse pull() {
        // Setting parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("fromImage", this.name);
        params.put("tag", "latest");
        params.put("labels", this.labels.toString());

        HttpResponse createImageResponse = Docker.post("images/create", "",
                params,
                Client.getClient().noBody(),
                "application/x-www-form-urlencoded");

        return createImageResponse;
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
}
