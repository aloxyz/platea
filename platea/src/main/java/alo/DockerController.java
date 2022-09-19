package alo;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DockerController {

    public static HttpResponse get(String endpoint, String id, Map<String, String> params) throws Exception {
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

    public static HttpResponse delete(String endpoint, String id) throws Exception {
        return 
            Client.getClient()
            .deleteResource(
                String.format("/%s/%s", endpoint, id),
                Client.getClient().noParameters());

    }

    public static HttpResponse listContainers() throws Exception {
        return get("containers", "", Client.getClient().noParameters());
    }

    public static HttpResponse inspectContainer(String id) throws Exception {
        return get("containers", id, Client.getClient().noParameters());
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


    public static HttpResponse deleteContainer(String id) throws Exception {
        return
            delete("containers", id);
    }


    public static HttpResponse pruneContainers() throws Exception {
        return 
        post("/containers/prune", "",
        Client.getClient().noParameters(),
        Client.getClient().noBody());
    }

    public static HttpResponse listImages(Map<String, String> params) throws Exception {
        return get("images", "", params);
    }

    public static HttpResponse inspectImage(String id) throws Exception {
        return get("images", id, Client.getClient().noParameters());
    }

    public static HttpResponse buildImageRemote(String name, String instance, String uri) throws Exception {
        HashMap<String, String> labels = new HashMap<String, String>();
        labels.put("service", "platea");
        labels.put("instance", instance);
        String jsonLabels = new JSONObject(labels).toJSONString();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("t", name);
        params.put("remote", uri);
        params.put("labels", jsonLabels);

        return
            DockerController.post("build", "",
            params,
            Client.getClient().noBody());
    }

    public static HttpResponse deleteImage(String id) throws Exception {
        return
            delete("images", id);
    }


    public static HttpResponse pruneImages() throws Exception {
        return 
        post("/images/prune", "",
        Client.getClient().noParameters(),
        Client.getClient().noBody());
    }

}
