package alo;

import java.net.http.HttpResponse;

public class Image implements IDockerController{
    private final String id;
    private final Instance instance; //get instance by instancename (label)
    private final String name;
    private String uri;

    Image(String id, String name, Instance instance) {
        this.id = id;
        this.name = name;
        this.instance = instance;
    }

    public HttpResponse buildRemote() throws Exception {
        return Images.buildRemote(name, instance.getName(), uri);
    }

    public HttpResponse inspect() throws Exception {
        return Images.inspect(id);
    }

    public HttpResponse delete() throws Exception {
        return Images.delete(name, "true");
    }

    public String getId() {
        return id;
    }

    public Instance getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }


}
