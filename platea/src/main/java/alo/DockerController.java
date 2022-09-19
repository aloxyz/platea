package alo;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DockerController {

    public static HttpResponse get(String endpoint, String id) throws Exception {
        if (!id.isEmpty()) {
            return
                Client.getClient()
                .getResource(
                    String.format("/%s/%s/json", endpoint, id),
                    Client.getClient().noParameters());
        } 
        
        else {
            return
                Client.getClient()
                .getResource(
                    String.format("/%s/json", endpoint),
                    Client.getClient().noParameters());
        }   
    }

    public static HttpResponse post(String endpoint, String id, Map<String, String> parameters, BodyPublisher body) throws Exception {
        if (!id.isEmpty()) {
            return
                Client.getClient()
                .postResource(
                    String.format("/%s/%s", endpoint, id),
                    parameters, body);
        }

        else {
            return
                Client.getClient()
                .postResource(
                    String.format("/%s", endpoint),
                    parameters, body);
        }
    }


    public static HttpResponse listContainers() throws Exception {
        return get("containers", "");
    }

    public static HttpResponse inspectContainer(String id) throws Exception {
        return get("containers", id);
    }

    public static HttpResponse createContainer(String name, JSONObject body) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);

        String jsonBody =
            new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(body);

        return
            DockerController.post("build", "",
            params,
            BodyPublishers.ofString(jsonBody)
            );
    }

    public static HttpResponse pruneImages() throws Exception {
        return 
        post("/images/prune", "",
        Client.getClient().noParameters(),
        Client.getClient().noBody());
    }

    public static HttpResponse listImages() throws Exception {
        return get("images", "");
    }

    public static HttpResponse inspectImage(String id) throws Exception {
        return get("images", id);
    }

    public static HttpResponse pruneContainers() throws Exception {
        return 
        post("/containers/prune", "",
        Client.getClient().noParameters(),
        Client.getClient().noBody());
    }

    public static HttpResponse buildImageRemote(String name, String uri) throws Exception {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("t", name+":platea");
        params.put("remote", uri);

        return
            DockerController.post("build", "",
            params,
            Client.getClient().noBody());
    }
}
