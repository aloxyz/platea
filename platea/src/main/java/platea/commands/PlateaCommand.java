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

    public static void main(final String[] args) {
        int status = new CommandLine(new PlateaCommand()).execute("jobs", "create", "-l", "-f", "/home/alo/Documenti/platea/sample.json", "--name", "picocli_job");
        System.exit(status);
    }

    public Integer call() throws Exception {
        return SUCCESS;
    }
}