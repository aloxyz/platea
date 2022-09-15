package alo;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "platea", description = "Docker container provisioning tool.", mixinStandardHelpOptions = true)
public class PlateaCommand implements Callable<Integer> {

    @Option(names = {"-f", "--fetch-instances"}, description = "fetch instance files from the remote repository.")
    boolean fetchInstances;

    @Option(names = {"-l", "--list-instances"}, description = "List available instances from the remote repository.")
    boolean listInstances;

    @Parameters(paramLabel = "INSTANCE", description = "The instance file.")
    File instanceFile;

    @Option(names = {"--build"}, description = "Build the specified instance.")
    boolean buildInstance;

    @Option(names = {"--start"}, description = "Start the specified instance.")
    boolean startInstance;

    @Option(names = {"--run"}, description = "Build and start the specified instance.")
    boolean runInstance;

    @Override
    public Integer call() throws Exception {
        Instance instance = null;
        Mapper mapper = new Mapper();

        if (listInstances) {
            System.out.println(ConsoleColors.BLUE_BRIGHT + "from gitlab.com/aloxyz/platea-configs: " + ConsoleColors.RESET);
            
            for (String i : Orchestrator.listRemoteInstances()) {
                System.out.println(ConsoleColors.YELLOW_UNDERLINED+i+ConsoleColors.RESET);
            }
        }

        if (fetchInstances) {
            Orchestrator.fetchRemoteInstances();
        }

        if (instanceFile != null) {
            if (buildInstance) {
                instance = Orchestrator.newInstance(instanceFile.getAbsolutePath());
                instance.start();
                instance.buildContainers();
                mapper.instanceToFile(instance, "instance.json");
            }
    
            if (startInstance) {
                if (instance != null) {
                    instance.startContainers();
                }
            }
    
            if (runInstance) {
                if (instance != null) {
                    instance.runContainers();
                }
            }
        }

        

        return 0;
    }
}
