package alo;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Instances {
    public static void fetchRemote() {
        String instancesPath = Config.getConfig().instancesPath();

        ProcessBuilder builder = new ProcessBuilder();
        System.out.println(ConsoleColors.BLUE_BRIGHT +  "Fetching..." +  ConsoleColors.RESET);
        //builder.inheritIO(); // Inherit stdout
        if (! new File(instancesPath).exists()) {
            // clone platea-configs repository in configs/
            String[] gitClone = {"/bin/sh", "-c", "git clone " + Config.getConfig().remoteRepositoryURL() + " " + instancesPath};
            builder.command(gitClone);
        
        } else {
            String[] gitPull = {"/bin/sh", "-c", "cd " + instancesPath + " && git pull"};
            builder.command(gitPull);
        }
        
        Process p;
        try {
            p = builder.start();
            p.waitFor(20, TimeUnit.SECONDS);
            System.out.println("Done");
        }

        catch (InterruptedException e) {
            System.out.println("Fetch process hanged for too long");
        }

        catch (IOException e) {
            System.out.println("I/O Error");
        }


        // DOWNLOADING TAR FROM REPO DOESN'T WORK, HTTP ERROR 406
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

    public static ArrayList<String> listRemote() {
        String instancesPath = Config.getConfig().instancesPath();

        if (! new File(instancesPath).exists()) {
            fetchRemote();
        }

        ArrayList<String> instances = new ArrayList<String>();

        // populate instances String Arraylist
        for (File f : Objects.requireNonNull(new File(instancesPath).listFiles())) {    // go through all jsons
            if (!f.getName().equals(".git") && f.getName().endsWith(".json")) {
                instances.add(f.getName().substring(0, f.getName().lastIndexOf('.')));
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

    public static ArrayList<String> listRunning() {
        /*
         * Returns an ArrayList of running containers IDs
         */
        String containersString = listContainers("", "running").body().toString();
        JSONArray containers = (JSONArray)JSONValue.parse(containersString);
        
        ArrayList<String> tmp = new ArrayList<>();

        for (JSONObject container : JSONController.JSONArrayToList(containers)) {
            JSONArray names = (JSONArray)container.get("Names");
            JSONObject labels = (JSONObject)container.get("Labels");

            try {
                tmp.add(
                    ConsoleColors.BLUE_BRIGHT + names.get(0).toString().substring(1) + ConsoleColors.RESET + "\t\t" + labels.get("instance").toString());
            }
            catch (NullPointerException e) {
            }
        }

        return tmp;
    }
    
    public static HttpResponse listImages(String instanceName) {
        HashMap<String,String> params = new HashMap<>();
        params.put("all", "true");
        if(instanceName.isEmpty()) {
            params.put("filters", "{\"label\":[\"instance\"]}");
        }
        else {
            params.put("filters", String.format("{\"label\":[\"instance=%s\"]}", instanceName));
        }
        return
            Docker.get("images", "", params);
    }

    public static HttpResponse listContainers(String instanceName, String status) {
        HashMap<String,String> params = new HashMap<>();
        params.put("all", "true");
        if(instanceName.isEmpty()) {
            params.put("filters", "{\"label\":[\"instance\"]}");
        }
        else {
            params.put("filters", String.format("{\"label\":[\"instance=%s\"]}", instanceName));
        }
        if(!status.isEmpty()) {
            params.put("filters", String.format("{\"status\": [\"%s\"]}", status));
        }


        return 
            Docker.get("containers", "", params);
    }

    public static HttpResponse pruneContainers() {
        return 
        Docker.post("/containers/prune", "",
        Client.getClient().noParameters(),
        Client.getClient().noBody(),
        "application/x-www-form-urlencoded");
    }

    public static HttpResponse pruneImages() {
        return 
            Docker.post("/images/prune", "",
            Client.getClient().noParameters(),
            Client.getClient().noBody(),
            "application/x-www-form-urlencoded");
    }
}
