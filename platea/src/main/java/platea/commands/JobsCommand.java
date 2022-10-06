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


    public Integer call() throws Exception {
        return 0;
    }
}