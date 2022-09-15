package alo;

import org.json.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Orchestrator {
    private List<Instance> instances = new ArrayList<>();
    

    public static void fetchRemoteInstances() throws Exception {
        String instancesPath = Config.getConfig().instancesPath();

        ProcessBuilder builder = new ProcessBuilder();
        System.out.println("Fetching...");
        // builder.inheritIO(); // Inherit stdout
        if (! new File(instancesPath).exists()) {
            // clone platea-configs repository in configs/
            String[] gitClone = {"/bin/sh", "-c", "git clone " + Config.getConfig().remoteRepositoryURL() + " " + instancesPath};
            builder.command(gitClone);
        
        } else {
            String[] gitPull = {"/bin/sh", "-c", "cd " + instancesPath + " && git pull"};
            builder.command(gitPull);
        }
        
        Process p = builder.start();
        p.waitFor(20, TimeUnit.SECONDS);

        // DOWNLOADING TAR FROM REPO DOESNT WORK, HTTP ERROR 406
        /*
        // Download configs archive
        FileIO.wget(this.configRepository, "tmp/configs.tar.gz");

        // Extract archive
        FileIO.extractTarball("tmp/configs.tar.gz", "tmp/");
        new File("tmp/configs.tar.gz").delete();
        new File("tmp/pax_global_header").delete();

        ArrayList<String> instances = new ArrayList<String>();
        */
    }

    public static ArrayList<String> listRemoteInstances() throws Exception {
        String instancesPath = Config.getConfig().instancesPath();

        if (! new File(instancesPath).exists()) {
            fetchRemoteInstances();
        }

        ArrayList<String> instances = new ArrayList<String>();

        // populate instances String Arraylist
        for (File f : new File(instancesPath).listFiles()) {    // go through all jsons
            if (f.getName() != ".git" && f.getName().endsWith(".json")) {
                instances.add(f.getName());
            }
            /*
            if (f.getName() != ".git" && f.getName().endsWith(".json")) {
                String instance = FileIO.readFile(f.getAbsolutePath());             // read json content
                
                JSONArray arr = new JSONArray(instance);                            // go through the json
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = new JSONArray(instance).getJSONObject(i);
                    instances.add(
                        obj.getString("platea_service_name"));                  // add the instance name to the ArrayList
                }
            }
             */
        }
        return instances;
    }

    public static Instance newInstance(String name) throws Exception {
        return
        new Mapper()
        .instanceFromFile(Config.getConfig().instancesPath() + name);
    }

    public static List listPlateaInstances() throws Exception {
        List<String> filter = new ArrayList<>();
        filter.add("platea_*");

        return
        Client
        .getClient()
        .getDockerClient()
        .listContainersCmd()
        .withShowAll(true)
        .withNameFilter(filter)
        .exec();
    }
}
