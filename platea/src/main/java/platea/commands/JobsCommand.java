package platea.commands;

import picocli.CommandLine;
import platea.ConsoleColors;
import platea.Job;
import platea.exceptions.CreateJobException;
import platea.exceptions.docker.DeleteJobException;
import platea.exceptions.docker.StartContainerException;
import platea.exceptions.docker.StopContainerException;

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

    @CommandLine.Option(
            names = {"-n", "--name"},
            description = "Name for the new job.",
            paramLabel = "<name>")
    String jobName;

/*    @CommandLine.Parameters(
            description = "Specified job name",
            paramLabel = "<job>")
    String jobName;*/

    @Override
    public Integer call() {
        try {
            if (delete) {
                System.out.print("Deleting job " + ConsoleColors.BLUE_BRIGHT + jobName + ConsoleColors.RESET + "... ");
                new Job(jobName).delete();
                System.out.print(ConsoleColors.GREEN_BRIGHT + "done" + ConsoleColors.RESET);

            } else if (purge) {
                System.out.print("Purging job " + ConsoleColors.BLUE_BRIGHT + jobName + ConsoleColors.RESET + "... ");

                new Job(jobName).purge();
                System.out.print(ConsoleColors.GREEN_BRIGHT + "done" + ConsoleColors.RESET);

            } else if (start) {
                System.out.print("Starting job " + ConsoleColors.BLUE_BRIGHT + jobName + ConsoleColors.RESET + "... ");

                new Job(jobName).start();
                System.out.print(ConsoleColors.GREEN_BRIGHT + "done" + ConsoleColors.RESET);

            } else if (stop) {
                System.out.print("Stopping job " + ConsoleColors.BLUE_BRIGHT + jobName + ConsoleColors.RESET + "... ");

                new Job(jobName).stop();
                System.out.print(ConsoleColors.GREEN_BRIGHT + "done" + ConsoleColors.RESET);
            }
        } catch (DeleteJobException | CreateJobException | StartContainerException | StopContainerException e) {
            System.out.println(ConsoleColors.RED + "\n" + e.getMessage() + ConsoleColors.RESET);
            System.exit(1);
        }

        return 0;
    }
}