package alo;

import java.io.File;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class Instances {

    public static void fetchRemote() throws Exception {
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

    public static ArrayList<String> listRemote() throws Exception {
        String instancesPath = Config.getConfig().instancesPath();

        if (! new File(instancesPath).exists()) {
            fetchRemote();
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

    public static void list() throws Exception {
        System.out.println("Platea containers: ");

        String containersString = Containers.list().body().toString();

        JSONArray containers = (JSONArray)JSONValue.parse(containersString);
        
        for(JSONObject container : JSONController.JSONArrayToList(containers)) {
            JSONObject labels = (JSONObject)container.get("Labels");
            
                System.out.println(ConsoleColors.PURPLE_BOLD + "instance: " + labels.get("instance").toString() + ConsoleColors.RESET);
                System.out.println("names: " + container.get("Names"));
                System.out.println("id: " + container.get("Id").toString());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String,Map> run(String configPath) throws Exception {
        HashMap<String, Map> instanceResponses = new HashMap<>();

        JSONObject config = JSONController.fileToJsonObject(Paths.get(configPath).toString());
        JSONObject containers = (JSONObject)config.get("containers");
    
        containers.keySet().forEach(key -> {
            try {
                Object value = containers.get(key);
                JSONObject container = (JSONObject) value;

                // SETUP
                String instanceName = config.get("instanceName").toString();
                String uri = container.get("endpoint").toString();
                
                JSONObject buildConfig = (JSONObject)container.get("config");

                //get name from image name
                String tmpImageName = buildConfig.get("Image").toString();
                String imageName = tmpImageName.substring(0, tmpImageName.lastIndexOf(":"));
                

                // START
                HttpResponse buildImageRemoteResponse = Images.buildRemote(imageName, instanceName, uri);
                HttpResponse createContainerResponse = Containers.create(imageName, instanceName, buildConfig);
                System.out.println(createContainerResponse.body().toString());
                String containerId = Docker.getFromResponse(createContainerResponse, "Id");
                HttpResponse startContainerResponse = Containers.start(containerId);

                HashMap<String,HttpResponse> responses = new HashMap<>();
                responses.put("buildImageRemote", buildImageRemoteResponse);
                responses.put("createContainer", createContainerResponse);
                responses.put("startContainer", startContainerResponse);

                instanceResponses.put(instanceName, responses);
            } catch (Exception e) {e.printStackTrace();}
        });

        return instanceResponses;
    }

    /*
    public static Map<String,HttpResponse> remove(String containerId) {
        HttpResponse stopContainerResponse = Containers.stop(containerId);
        HttpResponse deleteContainerResponse = Containers.delete(containerId, "true");
        HttpResponse deleteImageResponse = Images.delete(imageName, "true");
    }
    */
}
