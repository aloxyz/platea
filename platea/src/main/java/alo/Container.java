package alo;

import org.json.simple.JSONObject;

import java.net.http.HttpResponse;

public class Container {
    private final String id;
    private final String name;
    private final Instance instance; //get instance by instancename (label)
    private final JSONObject config;

    Container(Instance instance) {
        this.id = "id";
        this.name = "name";
        this.instance = instance;

        this.config = instance.getConfig(); //TODO
    }

    public HttpResponse create() throws Exception {
        return Containers.create(name, instance.getName(), config);
    }

    public HttpResponse inspect() throws Exception {
        return Containers.inspect(id);
    }

    public HttpResponse delete() throws Exception {
        return Containers.delete(id, "true");
    }

    public HttpResponse start() throws Exception {
        return Containers.start(id);
    }

    public HttpResponse stop() throws Exception {
        return Containers.stop(id);
    }
}
