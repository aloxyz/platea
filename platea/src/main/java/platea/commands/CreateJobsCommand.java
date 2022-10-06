package platea.commands;

import org.json.JSONObject;
import picocli.CommandLine;
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
            required = false)
    String jobName;

    @CommandLine.Option(
            names = {"-f", "--file"},
            description =
                    "Config file to build the job instance. " +
                    "If the --local flag is on, " +
                    "a fully qualified path for the json file must be specified instead.",

            paramLabel = "<config file name>",
            required = false)
    File file;

    @CommandLine.Option(
            names = {"-l", "--local"},
            description = "Use a local file.",
            paramLabel = "<name>",
            required = false)
    boolean local;

    @Override
    public Integer call() throws Exception {
        JSONObject config;
        if (local) {
            config = new JSONObject(Files.readString(file.toPath()));
        }
        else {

            config = new JSONObject(Files.readString(file.toPath()));

        }

        new Job(jobName, config);

        return 0;
    }
}
