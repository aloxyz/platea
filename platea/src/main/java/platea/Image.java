package platea;

import org.json.JSONObject;
import platea.exceptions.docker.CreateImageException;
import platea.exceptions.docker.DeleteContainerException;
import platea.exceptions.docker.DeleteImageException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static platea.Client.*;

public class Image {
    private final String name;
    private String endpoint;
    private boolean source;
    private boolean script;
    private JSONObject labels;

    Image(JSONObject config, String jobName) throws CreateImageException {
        this.name = config.getString("name");
        this.endpoint = config.getString("endpoint");
        this.source = config.getBoolean("source");
        this.script = config.getBoolean("script");

        // Labels object setup
        this.labels = new JSONObject();
        this.labels.put("service", "platea");
        this.labels.put("job", jobName);

        create();
    }

    Image(String name) {
        this.name = name;
    }

    public HttpResponse create() throws CreateImageException {
        HttpResponse createImageResponse;

        if (this.source) {
            createImageResponse = build();
        } else {
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
        String tmpPath = Config.getConfig().getEnv().get("TMP_PATH") + "/";
        String scriptsPath = Config.getConfig().getEnv().get("CONFIGS_PATH") + "/scripts";

        try {
            String trimmedName = this.name.substring(
                    this.name.lastIndexOf("/") + 1);

            // Pull image source from repo
            FileUtils.bash(String.format("git clone %s %s", this.endpoint, tmpPath + trimmedName));

            // Make tarball from source
            File tar = FileUtils.tar(tmpPath + trimmedName, tmpPath, trimmedName);
            Path tarPath = Paths.get(tar.getAbsolutePath());

            if (this.script) {
                FileUtils.bash(scriptsPath + this.name);
            }

            // Setting parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("t", trimmedName);
            params.put("labels", this.labels.toString());

            createImageResponse = dockerPost("build", "",
                    params,
                    HttpRequest.BodyPublishers.ofFile(tarPath),
                    "application/x-tar");

            FileUtils.cleanup();

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
        createImageResponse = dockerPost("build", "",
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

        HttpResponse createImageResponse = dockerPost("images/create", "",
                params,
                Client.getClient().noBody(),
                "application/x-www-form-urlencoded");

        return createImageResponse;
    }

    public HttpResponse delete(String force) throws DeleteImageException {
        HashMap<String, String> params = new HashMap<>();
        params.put("force", force);

        HttpResponse deleteImageResponse = dockerDelete("images", name, params);


        if (deleteImageResponse.statusCode() != 200) {
            String message = new JSONObject(deleteImageResponse.body().toString()).getString("message");
            throw new DeleteImageException("Could not delete image: " + message);
        }

        return deleteImageResponse;
    }

    public HttpResponse inspect() {
        return dockerGet("images", name, Client.getClient().noParameters());
    }
}
