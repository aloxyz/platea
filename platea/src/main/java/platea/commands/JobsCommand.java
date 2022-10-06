package platea.commands;

import picocli.CommandLine;

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
            names = {"--remove"},
            description = "Remove job instance.",
            paramLabel = "<name>")
    boolean delete;

    @CommandLine.Option(
            names = {"--purge"},
            description = "Remove job instance along with related docker images.",
            paramLabel = "<name>")
    boolean purge;

    @CommandLine.Option(
            names = {"--start"},
            description = "Start job.",
            paramLabel = "<name>")
    boolean start;

    @CommandLine.Option(
            names = {"--stop"},
            description = "Start job.",
            paramLabel = "<name>")
    boolean stop;

    public Integer call() throws Exception {
        return 0;
    }
}