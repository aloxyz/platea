package alo;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "platea", description = "Docker container provisioning tool.", mixinStandardHelpOptions = true)
public class PlateaCommand implements Callable<Integer> {
    public String configPath;

    @Parameters(paramLabel = "INSTANCE", description = "The instance name.")
    String instanceName;

    @Option(names = {"fetch"}, description = "fetch instance files from the remote repository.")
    boolean fetchInstances;

    @Option(names = {"ls"}, description = "List available instances from the remote repository.")
    boolean listInstances;

    @Option(names = {"ps"}, description = "List running instances.")
    boolean listRunningInstances;

    @Option(names = {"build"}, description = "Build the specified instance.")
    boolean buildInstance;

    @Option(names = {"start"}, description = "Start the specified instance.")
    boolean startInstance;

    @Option(names = {"run"}, description = "Build and start the specified instance.")
    boolean runInstance;

    @Option(names = {"stop"}, description = "Stop the specified instance.")
    boolean stopInstance;

    @Option(names = {"rm"}, description = "Remove the specified instance, along with beloning containers and images.")
    boolean removeInstance;

    @Override
    public Integer call() throws Exception {
        if(!instanceName.isEmpty()) {
            
            configPath = new File(
                Config.getConfig().instancesPath() + instanceName + ".json")
                .getAbsolutePath();
        }
        System.out.println(
            configPath
        );
        if (fetchInstances) {
            Instances.fetchRemote();
        }
        if (listInstances) {
            System.out.println(
                Instances.listRemote().toString()
            );
        }
        if (listRunningInstances) {
            System.out.println(
                Instances.listRunning().toString()
            );
        }
        if (buildInstance) {
            Instances.buildImages(configPath);
            Instances.createContainers(configPath);
        }
        if (startInstance) {

            Instances.startContainers(instanceName);
        }
        if (runInstance) {
            Instances.run(configPath);
        }
        if (stopInstance) {
            Instances.stopContainers(instanceName);
        }
        if (removeInstance) {
            Instances.deleteContainers(instanceName);
        }

        return 0;
    }
}
