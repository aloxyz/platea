package platea.commands;

import picocli.CommandLine;
import platea.ConsoleColors;
import platea.Jobs;
import platea.exceptions.database.GetException;

import java.sql.SQLException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "list",
        aliases = {"ls", "l"},
        header = "List platea jobs.",
        description = "If --name is not specified, all platea jobs will be listed.",
        optionListHeading= "\nOptions\n",
        mixinStandardHelpOptions = true,
        requiredOptionMarker = '*',
        subcommands = {CreateJobsCommand.class})
public class ListCommand implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-n", "--name"},
            description = "Job to inspect",
            paramLabel = "<name>")
    String jobName;

    @Override
    public Integer call() {
        if (jobName == null) {
            Jobs.list();

        } else {
            Jobs.list(jobName);
        }

        return 0;
    }
}