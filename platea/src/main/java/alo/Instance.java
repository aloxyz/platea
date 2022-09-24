package alo;

import org.json.simple.JSONObject;

import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public Map<String, Map> delete() throws Exception {
        return Instances.delete(name);
    }

    public Map<String,Map> run() throws Exception {
        return Instances.run(config);
    }

    public Map<String, HttpResponse> deleteContainers() throws Exception {
        return Instances.deleteContainers(name);
    }

    public Map<String, HttpResponse> deleteImages() throws Exception {
        return Instances.deleteImages(name);
    }

    public Map<String, HttpResponse> buildImages() throws Exception {
        return Instances.buildImages(config);
    }

    public Map<String, String> createContainers() throws Exception {
        return Instances.createContainers(config);
    }

    public Map<String, HttpResponse> startContainers() throws Exception {
        return Instances.startContainers(name);
    }

    public Map<String, HttpResponse> stopContainers() throws Exception {
        return Instances.stopContainers(name);
    }

    public String getName() {
        return name;
    }

    public JSONObject getConfig() {
        return config;
    }
}
