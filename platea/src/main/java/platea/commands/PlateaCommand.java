package platea.commands;

import picocli.CommandLine;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "platea",
        header = "Platea",
        description = "Docker container provisioning tool",
        footerHeading = "\nCopyright\n",
        footer = "Developed by Gabriele Aloisio",
        version = "0.3.4",
        optionListHeading = "\nOptions\n",
        commandListHeading = "\nSubcommands\n",
        mixinStandardHelpOptions = true,
        requiredOptionMarker = '*',
        subcommands = {JobsCommand.class})
public class PlateaCommand implements Callable<Integer> {
    final Integer SUCCESS = 0;
    final Integer FAILURE = 1;

    @CommandLine.Option(
            names = {"-v", "--verbose"},
            description = "Flag made for debug. Prints additional information.")
    boolean verbose;

    @CommandLine.Option(
            names = {"--fetch-configs"},
            description = "Fetch job configs from the official remote repository.")
    boolean fetch;

    @CommandLine.Option(
            names = {"--list-configs"},
            description = "List available job configs downloaded from the official remote repository.")
    boolean list;

    @CommandLine.Option(
            names = {"--ps"},
            description = "List running platea jobs.")
    boolean ps;

    public static void main(final String[] args) {
        int status = new CommandLine(new PlateaCommand()).execute(args);
        System.exit(status);
    }

    public Integer call() {
        return SUCCESS;
    }
}