package platea;

import org.json.JSONObject;
import platea.exceptions.docker.CreateImageException;
import platea.exceptions.docker.DeleteImageException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;

import static platea.Client.*;

public class Image {
    private final String name;
    private String endpoint;
    private boolean source;
    private JSONObject labels;
    private File script;

    Image(String name) {
        this.name = name;
    }

    Image(JSONObject config, String jobName) throws CreateImageException {
        this.name = config.getString("name");
        this.endpoint = config.getString("endpoint");
        this.source = config.getBoolean("source");

        // Labels object setup
        this.labels = new JSONObject();
        this.labels.put("service", "platea");
        this.labels.put("job", jobName);

        create();
    }

    Image(JSONObject config, File script, String jobName) throws CreateImageException {
        /* Create an image given a configuration script */

        this.name = config.getString("name");
        this.endpoint = config.getString("endpoint");
        this.source = config.getBoolean("source");
        this.script = script;

        // Labels object setup
        this.labels = new JSONObject();
        this.labels.put("service", "platea");
        this.labels.put("job", jobName);

        create();
    }

    public HttpResponse create() throws CreateImageException {
        System.out.print("Creating image " + this.name + "... ");

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

        System.out.println(ConsoleColors.GREEN_BRIGHT + " done" + ConsoleColors.RESET);
        return createImageResponse;
    }

    public HttpResponse build() {
        /* Build image from source */

        HttpResponse createImageResponse = null;

        try {
            String tmpPath = Config.getConfig().getEnv().get("TMP_PATH") + "/";
            new File(tmpPath);

            // Pull image source from repo
            FileUtils.bash(String.format("git clone %s %s", this.endpoint, tmpPath + this.name));

            // Run script if necessary
            if (this.script != null) {
                FileUtils.bash(this.script.getAbsolutePath());
            }

            // Make tarball from source
            File tar = FileUtils.tar(tmpPath + this.name, tmpPath, this.name);
            Path tarPath = Paths.get(tar.getAbsolutePath());

            // Setting parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("t", this.name);
            params.put("labels", this.labels.toString());

            createImageResponse =
                    getClient().postResource("/build",
                            params,
                            HttpRequest.BodyPublishers.ofFile(tarPath),
                            "application/x-tar");

            // tmp directory cleanup
            Files.walk(Paths.get(tmpPath))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

        } catch (FileNotFoundException e) {
            System.out.println("Tarball was not found");
            System.exit(1);

        } catch (IOException e) {
            System.out.println("Could not run setup script");
            System.exit(1);
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
        createImageResponse =
                getClient().postResource("/build",
                        params,
                        getClient().noBody(),
                        "application/x-www-form-urlencoded");

        return createImageResponse;
    }

    public HttpResponse pull() {
        // Setting parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("fromImage", this.name);
        params.put("tag", "latest");
        params.put("labels", this.labels.toString());

        return getClient().postResource("/images/create",
                params,
                getClient().noBody(),
                "application/x-www-form-urlencoded");
    }

    public HttpResponse delete(String force) throws DeleteImageException {
        HashMap<String, String> params = new HashMap<>();
        params.put("force", force);

        HttpResponse deleteImageResponse = getClient().deleteResource("/images/" + this.name, params);


        if (deleteImageResponse.statusCode() != 200) {
            String message = new JSONObject(deleteImageResponse.body().toString()).getString("message");
            throw new DeleteImageException("Could not delete image: " + message);
        }

        return deleteImageResponse;
    }

    public HttpResponse inspect() {
        return getClient().getResource("/images/" + this.name + "/json", Client.getClient().noParameters());
    }

    public String getName() {
        return name;
    }


}
