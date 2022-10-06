package platea;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static Config config;
    private final String configPath = System.getenv("HOME") + ".platearc";
    //private final String basePath = System.getenv("XDG_CONFIG_HOME") + "/platea";
    private final String basePath = "/home/alo/.config/platea";
    private final String jobConfigsPath = basePath + "/configs/";
    private final String containersPath = basePath + "/containers/";

    private final String tmpPath = basePath + "/tmp/";

    private final String scriptsPath = basePath + "/scripts/";
    Dotenv env = Dotenv.configure()
    .directory(basePath + "/.env")
    .ignoreIfMalformed()
    .ignoreIfMissing()
    .load();
    private final String remoteRepositoryURL = "git@gitlab.com:aloxyz/platea-configs.git";

    private final String databaseURL = "http://localhost:3000/instances";
    private final String dockerSocket = "unix:/var/run/docker.sock";
    private final String dockerURL = "localhost:2375";
    private Config() {
    }

    public static synchronized Config getConfig() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public String basePath() {
        return this.basePath;
    }

    public String jobConfigsPath() {
        return this.jobConfigsPath;
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

    public String getTmpPath() {
        return tmpPath;
    }

    public String dockerURL() {
        return this.dockerURL;
    }

    public Dotenv getEnv() {
        return env;
    }
}
