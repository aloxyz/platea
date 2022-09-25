package alo;

import org.json.simple.JSONObject;

import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Instance {
    private final String name;
    private ArrayList<Container> containers;
    private ArrayList<Image> images;
    private final JSONObject config;

    Instance(String configPath) throws Exception {
        this.config = JSONController.fileToJsonObject(Paths.get(configPath).toString());
        this.name = config.get("instanceName").toString();
    }

    public ArrayList<String> listRunning() throws Exception {
        return Instances.listRunning(name);
    }

    public ArrayList<Map> delete() throws Exception {
        ArrayList<Map> responses = new ArrayList<>();

        responses.add(deleteContainers());
        responses.add(deleteImages());

        return responses;
    }

    public ArrayList<Map> run() throws Exception {
        ArrayList<Map> responses = new ArrayList<>();

        responses.add(buildImages());
        responses.add(createContainers());
        responses.add(startContainers());

        return responses;        
    }

    public Map<String, HttpResponse> deleteContainers() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Container c : containers) {
            responses.put(c.getId(), c.delete());
        }

        return responses;
    }

    public Map<String, HttpResponse> deleteImages() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Image i : images) {
            responses.put(i.getId(), i.delete());
        }

        return responses;
    }

    public Map<String, HttpResponse> buildImages() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Image i : images) {
            responses.put(i.getId(), i.buildRemote());
        }

        return responses;
    }

    public Map<String, HttpResponse> createContainers() throws Exception {
        HashMap<String, HttpResponse> responses = new HashMap<>();

        for (Container c : containers) {
            responses.put(c.getId(), c.create());
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
