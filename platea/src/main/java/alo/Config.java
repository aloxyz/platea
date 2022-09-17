package alo;

public class Config {
    private static Config config;
    private String configPath = System.getenv("HOME") + ".platearc";
    private String basePath = System.getenv("XDG_CONFIG_HOME") + "/platea";
    private String instancesPath = basePath + "/instances/";
    private String containersPath = basePath + "/containers/";
    private String scriptsPath = basePath + "/scripts/";

    private String remoteRepositoryURL = "git@gitlab.com:aloxyz/platea-configs.git";
    private String databaseURL = "http://localhost:3000/instances";
    private String dockerSocket = "http://localhost:2375";
    private Config() throws Exception {
    } 

    public static void main(String[] args) throws Exception {
        ProcessBuilder builder = new ProcessBuilder();
        
        String[] setEnv = {"/bin/sh", "-c", "export PLATEA_BASE_PATH=" + Config.getConfig().basePath()};
        builder.command(setEnv);
    }
 
    public static synchronized Config getConfig() throws Exception {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public String basePath() {
        return this.basePath;
    }

    public String instancesPath() {
        return this.instancesPath;
    }

    public String remoteRepositoryURL() {
        return this.remoteRepositoryURL;   
    }

    public String containersPath() {
        return this.containersPath;
    }
    
    public String scriptsPath() {
        return this.scriptsPath;
    }

    public String databaseURL() {
        return this.databaseURL;
    }

    public String dockerSocket() {
        return this.dockerSocket;
    }
}
