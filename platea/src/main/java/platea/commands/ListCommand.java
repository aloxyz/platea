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
        optionListHeading = "\nOptions\n",
        mixinStandardHelpOptions = true,
        requiredOptionMarker = '*',
        subcommands = {CreateJobsCommand.class})
public class ListCommand implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-n", "--name"},
            description = "Inspect a particular job",
            paramLabel = "<name>")
    String jobName;

    @CommandLine.Option(
            names = {"--running"},
            description = "List only running platea containers")
    boolean running;

    @Override
    public Integer call() {
            Jobs.list();

        return 0;
    }
}