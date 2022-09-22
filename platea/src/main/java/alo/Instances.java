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

    public static Map<String,Map> delete(String instanceName) throws Exception {
        HashMap<String,Map> responses = new HashMap<>();
        responses.put("containers", deleteContainers(instanceName));
        responses.put("images", deleteImages(instanceName));

        return responses;
    }

    public static Map<String,Map> run(String configPath) throws Exception {
        HashMap<String, Map> responses = new HashMap<>();

        responses.put("buildImages", Instances.buildImages(configPath));
        
        Map<String, String> createContainersResponse = Instances.createContainers(configPath);
        ArrayList<String> ids = new ArrayList<>(createContainersResponse.values());
        
        responses.put("createContainers", createContainersResponse);
        responses.put("startContainers", Instances.startContainers(ids));

        return responses;
    }

    public static Map<String,HttpResponse> deleteContainers(String instanceName) throws Exception {
        /*
         * Returns a map of Containers.delete() responses for each deleted container.
         * Container id is the key for an HttpResponse value. 
         * 
         * deleteContainers().get("container-id") => HttpResponse
         */
        HashMap<String,HttpResponse> responses = new HashMap<>();
        
        String containersString = Containers.list(instanceName).body().toString();
        JSONArray containers = (JSONArray)JSONValue.parse(containersString);
        HttpResponse deleteContainerResponse;

        for(JSONObject container : JSONController.JSONArrayToList(containers)) {
            String id = container.get("Id").toString();
            deleteContainerResponse = Containers.delete(id, "true");
            responses.put(id, deleteContainerResponse);
        }

        return responses;
    }

    public static Map<String,HttpResponse> deleteImages(String instanceName) throws Exception {
        /*
         * Returns a map of Images.delete() responses for each deleted image.
         * Image id is the key for an HttpResponse value. 
         * 
         * deleteImages().get("image-id") => HttpResponse
         */
        HashMap<String,HttpResponse> responses = new HashMap<>();
        
        String imagesString = Images.list(instanceName).body().toString();
        JSONArray images = (JSONArray)JSONValue.parse(imagesString);
        HttpResponse deleteImageResponse;

        for(JSONObject image : JSONController.JSONArrayToList(images)) {
            String id = image.get("Id").toString();
            deleteImageResponse = Images.delete(id, "true");
            responses.put(id, deleteImageResponse);
        }

        return responses;
    }

    @SuppressWarnings("unchecked")
    public static Map<String,HttpResponse> buildImages(String configPath) throws Exception {
        /*
         * Returns a map of Images.buildRemote() responses for each image.
         * Image name is the key for an HttpResponse value. 
         * 
         * build().get("image-name") => HttpResponse
         */
        HashMap<String,HttpResponse> responses = new HashMap<>();
        
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

                responses.put(imageName, buildImageRemoteResponse);
            } catch (Exception e) {e.printStackTrace();}

        });
        return responses;
    }

    @SuppressWarnings("unchecked")
    public static Map<String,String> createContainers(String configPath) throws Exception {
        /*
         * Returns a map of Containers.create() IDs for each created container.
         * Container name is the key for an ID value. 
         * 
         * create().get("container-name") => String ID
         */
        HashMap<String,String> ids = new HashMap<>();
        
        JSONObject config = JSONController.fileToJsonObject(Paths.get(configPath).toString());
        JSONObject containers = (JSONObject)config.get("containers");

        containers.keySet().forEach(key -> {
            try {
                Object value = containers.get(key);
                JSONObject container = (JSONObject) value;

                // SETUP
                String instanceName = config.get("instanceName").toString();
                
                JSONObject buildConfig = (JSONObject)container.get("config");

                //get name from image name
                String tmp = buildConfig.get("Image").toString();
                String containerName = tmp.substring(0, tmp.lastIndexOf(":"));
                
                // START
                HttpResponse createContainerResponse = Containers.create(containerName, instanceName, buildConfig);
                
                String id = Docker.getFromResponse(createContainerResponse, "Id");
                
                ids.put(containerName, id);
            } catch (Exception e) {e.printStackTrace();}

        });
        return ids;
    }

    public static Map<String, HttpResponse> startContainers(ArrayList<String> ids) throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (String id : ids) {
            responses.put(id, Containers.start(id));
        }

        return responses;
    }

    public static Map<String, HttpResponse> stopContainers(String instanceName) throws Exception {
        HashMap<String,HttpResponse> responses = new HashMap<>();
        
        String containersString = Containers.list(instanceName).body().toString();
        JSONArray containers = (JSONArray)JSONValue.parse(containersString);
        HttpResponse stopContainerResponse;

        for(JSONObject container : JSONController.JSONArrayToList(containers)) {
            String id = container.get("Id").toString();
            stopContainerResponse = Containers.stop(id);
            responses.put(id, stopContainerResponse);
        }

        return responses;
    }
}
