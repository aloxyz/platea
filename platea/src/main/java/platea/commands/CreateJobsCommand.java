package platea.commands;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import picocli.CommandLine;
import platea.Database;
import platea.FileUtils;
import platea.Job;
import platea.exceptions.CreateJobException;
import platea.exceptions.database.GetException;
import platea.exceptions.docker.DeleteJobException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.concurrent.Callable;

import static platea.Config.getConfig;

@CommandLine.Command(
        name = "create",
        //aliases = {},
        header = "Create a platea job.",
        optionListHeading = "\nOptions\n",
        mixinStandardHelpOptions = true,
        requiredOptionMarker = '*')
public class CreateJobsCommand implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-n", "--name"},
            description = "Name for the new job.",
            paramLabel = "<name>",
            required = true)
    String jobName;

    @CommandLine.Option(
            names = {"-f", "--file"},
            description =
                    "Configuration file to build the job instance. " +
                            "If the --local flag is on, " +
                            "a fully qualified path for the json file must be specified instead.",
            paramLabel = "<config>",
            required = true)
    String configName;

    @CommandLine.Option(
            names = {"-l", "--local"},
            description = "Use a local file.",
            paramLabel = "<name>")
    boolean local;

    @CommandLine.Option(
            names = {"-c", "--context"},
            description =
                    "Build context path. Required if the specified configuration needs auxiliary scripts to build images.",
            paramLabel = "<context>")
    File context;

    @Override
    public Integer call() {
        try {
            Database db = Database.getDatabase();
            Dotenv env = getConfig().getEnv();

            if (db.getJob(jobName) != null) { // if job exists in database
                new Job(jobName);
            }

            else {
                JSONObject config;

                if (local) {
                    config = new JSONObject(Files.readString(
                            new File(configName).toPath()));

                } else {
                    // Read config from repo into String
                    String baseUrl = env.get("REMOTE_URL") + "/-/raw/main/";
                    config = new JSONObject(FileUtils.get(baseUrl + configName));

                    JSONArray images = config.getJSONArray("images");
                    HashMap<String, File> scripts = new HashMap<>();

                    // If image in config.images has a script
                    for (int i = 0; i < images.length(); i++) {
                        JSONObject image = (JSONObject) images.get(i);

                        if (!image.isNull("script")) {
                            String scriptName = image.getString("script");

                            // Build url to download the script from
                            FileUtils.wget(
                                    baseUrl + scriptName,
                                    env.get("TMP_PATH") + scriptName
                            );

                            scripts.put(
                                    scriptName,
                                    new File(env.get("TMP_PATH") + scriptName));
                        }
                    }

                    new Job(jobName, config, scripts);
                    return 0;
                }

                new Job(jobName, config, context);
            }
        } catch (GetException | IOException e) {
            System.out.println(e.getMessage());

        } catch (CreateJobException e) {
            System.out.println(e.getMessage());

            try {
                new Job(jobName).purge();

            } catch (CreateJobException | DeleteJobException p) {
                System.out.println(p.getMessage());
            }
        }

        return 0;
    }
}
