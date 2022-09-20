package alo;

import java.net.http.HttpResponse;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class Containers {
    public static HttpResponse create(String path) throws Exception {
        JSONObject body = new JSONObject();
        body = JSONController.fileToJsonObject(path);

        HttpResponse response =
            DockerController.createContainer("", body);
        return response;
    }

    public static HttpResponse list() throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"service=platea\"]}");

        HttpResponse response =
            DockerController.listContainers(params);

        return response;
    }

    public static HttpResponse inspect(String id) throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"service=platea\"]}");

        HttpResponse response =
            DockerController.inspectContainer(id, params);

        return response;
    }

    public static HttpResponse delete(String id) throws Exception {
        HttpResponse response =
            DockerController.deleteContainer(id, "true");
        return response;
    }
}
