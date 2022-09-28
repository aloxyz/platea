package alo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


import java.io.File;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class Instance {
    private String name;
    private ArrayList<Container> containers;
    private ArrayList<Image> images;
    private final JSONObject config;

    @SuppressWarnings("unchecked")
    Instance(String configPath) throws Exception {
        this.config = JSONController.fileToJsonObject(Paths.get(configPath).toString());
        this.name = config.get("instanceName").toString();

        Database.getDatabase().instanceHandler(this);

        JSONObject containers = (JSONObject)config.get("containers");

        this.containers = new ArrayList<>();
        this.images = new ArrayList<>();

        containers.keySet().forEach(key -> {
            try {
                Object value = containers.get(key);
                JSONObject container = (JSONObject) value;                
                JSONObject buildConfig = (JSONObject)container.get("config");

                String tmp = buildConfig.get("Image").toString();
                String name = tmp.substring(0, tmp.lastIndexOf(":"));

                String uri = container.get("endpoint").toString();


                System.out.println(key.toString());
                System.out.println(container.toJSONString());
                System.out.println(buildConfig.toJSONString());
                System.out.println(tmp);
                System.out.println(name);
                System.out.println(uri);

                this.images.add(new Image(name, this, uri));
                this.containers.add(new Container(name, this, buildConfig));


            } catch (Exception e) {e.printStackTrace();}
        });
    }

    public void setName(String name) {
        this.name = name;
    }

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
        System.out.println("Done");

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

    public static ArrayList<String> listRemote() throws Exception {
        String instancesPath = Config.getConfig().instancesPath();

        if (! new File(instancesPath).exists()) {
            fetchRemote();
        }

        ArrayList<String> instances = new ArrayList<String>();

        // populate instances String Arraylist
        for (File f : Objects.requireNonNull(new File(instancesPath).listFiles())) {    // go through all jsons
            if (!f.getName().equals(".git") && f.getName().endsWith(".json")) {
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

    public static ArrayList<String> listRunning() throws Exception {
        /*
         * Returns an ArrayList of running containers IDs
         */
        String containersString = Container.list("running").body().toString();
        JSONArray containers = (JSONArray)JSONValue.parse(containersString);
        
        ArrayList<String> tmp = new ArrayList<>();

        for (JSONObject container : JSONController.JSONArrayToList(containers)) {
            tmp.add(container.get("Id").toString());
        }

        return tmp;
    }

    public ArrayList<Map> delete() throws Exception {
        ArrayList<Map> responses = new ArrayList<>();

        responses.add(deleteContainers());
        responses.add(deleteImages());

        Database.getDatabase().deleteInstance(this);

        return responses;
    }

    public Map<String, HttpResponse> deleteContainers() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Container c : containers) {
            responses.put(c.getId(), c.delete("true"));
        }

        return responses;
    }

    public Map<String, HttpResponse> deleteImages() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Image i : images) {
            responses.put(i.getName(), i.delete("true"));
        }

        return responses;
    }

    public Map<String, HttpResponse> startContainers() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Container c : containers) {
            responses.put(c.getId(), c.start());
        }

        return responses;
    }

    public Map<String, HttpResponse> stopContainers() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Container c : containers) {
            responses.put(c.getId(), c.stop());
        }

        return responses;
    }

    public String getName() {
        return name;
    }

    public JSONObject getConfig() {
        return config;
    }

}
