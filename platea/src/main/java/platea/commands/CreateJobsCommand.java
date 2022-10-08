package platea.commands;

import org.json.JSONObject;
import picocli.CommandLine;
import platea.Config;
import platea.Database;
import platea.Job;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

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
    File configFile;

    @CommandLine.Parameters(
            description =
                    "Build context path. Required if the specified configuration needs auxiliary scripts to build images.",
            paramLabel = "<context>")
    File context;

    @CommandLine.Option(
            names = {"-l", "--local"},
            description = "Use a local file.",
            paramLabel = "<name>")
    boolean local;

    @Override
    public Integer call() throws Exception {
        Database db = Database.getDatabase();

        if (db.getJob(jobName) != null) { // if job exists in database
            new Job(jobName);

        } else {
            JSONObject config;

            if (local) {
                config = new JSONObject(Files.readString(configFile.toPath()));

            } else {
                String path = Config.getConfig().getEnv().get("CONFIGS_PATH") + configFile.getName();
                config = new JSONObject(Files.readString(Paths.get(path)));
            }

            new Job(jobName, config, context);
        }

        return 0;
    }
}
