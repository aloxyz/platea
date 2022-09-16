package alo;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Volume;
import com.google.common.io.Files;

public class Container {
    private String platea_service_name;
    private String container_name;
    private String endpoint;
    private String env;
    private String volume;
    private String port;
    private String image;
    private Boolean script;

    private CreateContainerResponse container;

    Container() throws Exception {

    }

    public String getPlateaServiceName() {
        return this.platea_service_name;
    }

    public String getContainerName() {
        return this.container_name;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getEnv() {
        return this.env;
    }

    public String getVolume() {
        return this.volume;
    }

    public String getPort() {
        return this.port;
    }

    public String getImage() {
        return this.image;
    }

    public boolean hasScript() {
        return this.script;
    }

    public CreateContainerResponse getContainer() {
        return this.container;
    }

    public void InitializeFromLHM(LinkedHashMap<String,Object> lhm) {
        //Initialize name and endpoint given a LinkedHashMap

        this.platea_service_name = lhm.get("platea_service_name").toString();
        this.container_name = lhm.get("container_name").toString();
        this.endpoint = lhm.get("endpoint").toString();
        
        if (lhm.get("env") != null) {
            this.env = lhm.get("env").toString();   
        }

        if (lhm.get("volume") != null) {
            this.volume = lhm.get("volume").toString();
        }

        this.port = lhm.get("port").toString();
        
        this.image = lhm.get("image").toString();
        
        this.script = (boolean)lhm.get("script");
    }

    public void runScript() throws Exception {
        System.out.println("Running script for service: " + getPlateaServiceName());
        ProcessBuilder builder = new ProcessBuilder();
        builder.inheritIO();
        String[] cmd = {"/bin/sh", "-c", Config.getConfig().scriptsPath() + this.platea_service_name + ".sh"};
        builder.command(cmd);
        Process p = builder.start();
        p.waitFor();
        System.out.println(p+" exited with value "+p.exitValue());
        /*
        String[] cmd = new String[]{ "/bin/sh", "scripts/"+this.platea_service_name+".sh" };
        Runtime.getRuntime().exec(cmd);
        */
    }

    public void importScript() throws Exception {
        /*
         * Moves script file from platea/instances/scripts to platea/scripts 
         */
        File scriptFile = new File(Config.getConfig().instancesPath() + "scripts/" + getPlateaServiceName() + ".sh");
        File destPath = new File(Config.getConfig().scriptsPath() + getPlateaServiceName() + ".sh");

        Files.move(scriptFile, destPath);
    }

    public String getID() {
        System.out.println(this.container.getId());
        return this.container.getId();
    }

    public void build() throws Exception {
        if (
            getPlateaServiceName()  != null && 
            getEnv()                != null && 
            getVolume()             != null && 
            getPort()               != null) {

            System.out.println("Building container: " + getPlateaServiceName());
            CreateContainerResponse container =
            Client
            .getClient()
            .getDockerClient()
            .createContainerCmd(this.image)
            
            .withName("platea_" + this.container_name)
            .withEnv(this.env)
            .withVolumes(new Volume(this.volume))
            .withPortSpecs(this.port)
            .exec();

            if (hasScript()) {
                runScript();
            }

        this.container = container;
        }        
    }

    public void start() throws Exception {
        System.out.println("Starting container: " + getPlateaServiceName());
        Client
        .getClient()
        .getDockerClient()
        .startContainerCmd
        (getID());
    }

    public void run() throws Exception {
        build();
        start();
    }

    public void stop() throws Exception {
        System.out.println("Stopping container: " + getPlateaServiceName());
        Client
        .getClient()
        .getDockerClient()
        .stopContainerCmd
        (getID());
    }

    public void delete() throws Exception {
        System.out.println("Deleting container: " + getPlateaServiceName());
        Client
        .getClient()
        .getDockerClient()
        .removeContainerCmd
        (getID())
        .exec();
    }

    public void fetchSource() throws Exception {
        String containersPath = Config.getConfig().containersPath();
        String archivePath = containersPath + "/" + getPlateaServiceName() + ".zip";

        System.out.println("Fetching source for service: " + getPlateaServiceName());
        FileIO.wget(endpoint, archivePath);
        FileIO.extractArchive(archivePath, containersPath + "/" + getPlateaServiceName());
        
        new File(archivePath).delete();
    }
}
