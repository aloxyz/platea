package alo;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "platea", description = "Docker container provisioning tool.", mixinStandardHelpOptions = true)
public class PlateaCommand implements Callable<Integer> {

    @Parameters(paramLabel = "INSTANCE", description = "The instance name.")
    String instanceName;

    @Option(names = {"-f", "--fetch-instances"}, description = "fetch instance files from the remote repository.")
    boolean fetchInstances;

    @Option(names = {"-l", "--list-instances"}, description = "List available instances from the remote repository.")
    boolean listInstances;

    @Option(names = {"-b", "--build"}, description = "Build the specified instance.")
    boolean buildInstance;

    @Option(names = {"--start"}, description = "Start the specified instance.")
    boolean startInstance;

    @Option(names = {"-r", "--run"}, description = "Build and start the specified instance.")
    boolean runInstance;

    @Option(names = {"--stop"}, description = "Stop the specified instance.")
    boolean stopInstance;

    @Option(names = {"-d", "--delete"}, description = "Delete the specified instance.")
    boolean deleteInstance;

    @Override
    public Integer call() throws Exception {
        if (fetchInstances) {
            Instances.fetchRemote();
        }
        if (listInstances) {
            Instances.listRemote();
        }
        if (buildInstance) {
            String configPath = new File(
                Config.getConfig().instancesPath() + instanceName + ".json")
                .getAbsolutePath();

            Instances.buildImages(configPath);
            Instances.createContainers(configPath);
        }
        if (startInstance) {

            Instances.startContainers(instanceName);
        }
        if (runInstance) {
            Instances.run(instanceName);
        }
        if (stopInstance) {
            Instances.stopContainers(instanceName);
        }
        if (deleteInstance) {
            Instances.deleteContainers(instanceName);
        }


        return 0;
    }
}
