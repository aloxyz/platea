package platea.commands;

import picocli.CommandLine;
import platea.Container;
import platea.Database;
import platea.Job;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "jobs",
        //aliases = {},
        header = "Manage platea jobs.",
        optionListHeading = "\nOptions\n",
        mixinStandardHelpOptions = true,
        requiredOptionMarker = '*',
        subcommands = {CreateJobsCommand.class})
public class JobsCommand implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--delete"},
            description = "Delete a job instance.",
            paramLabel = "<name>")
    boolean delete;

    @CommandLine.Option(
            names = {"--purge"},
            description = "Delete a job instance along with its related docker images.",
            paramLabel = "<name>")
    boolean purge;

    @CommandLine.Option(
            names = {"--start"},
            description = "Start a job.",
            paramLabel = "<name>")
    boolean start;

    @CommandLine.Option(
            names = {"--stop"},
            description = "Stop a job.",
            paramLabel = "<name>")
    boolean stop;

    @CommandLine.Parameters(
            description = "Specified job name",
            paramLabel = "<job>")
    String jobName;

    @Override
    public Integer call() throws Exception {
        Database db = Database.getDatabase();

        if (delete) {
            new Job(jobName).delete();
        } else if (purge) {
            new Job(jobName).purge();
        } else if (start) {
            //new Job(jobName).start();
        } else if (stop) {
            //new Job(jobName).stop();
        }

        return 0;
    }
}