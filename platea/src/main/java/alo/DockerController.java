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
                    params);
        } 
        
        else {
            return
                Client.getClient()
                .getResource(
                    String.format("/%s/json", endpoint),
                    params);
        }   
    }

    public static HttpResponse post(String endpoint, String id, Map<String, String> parameters, BodyPublisher body, String headers) throws Exception {
        if (!id.isEmpty()) {
            return
                Client.getClient()
                .postResource(
                    String.format("/%s/%s", endpoint, id),
                    parameters, body, headers);
        }

        else {
            return
                Client.getClient()
                .postResource(
                    String.format("/%s", endpoint),
                    parameters, body, headers);
        }
    }

    public static HttpResponse delete(String endpoint, String id, Map<String, String> parameters) throws Exception {
        return 
            Client.getClient()
            .deleteResource(
                String.format("/%s/%s", endpoint, id),
                parameters);

    }

    public static String getFromResponse(HttpResponse response, String key) {
        return
        JSONController.stringToJSONObject(
                response
                .body()
                .toString())
                .get(key).toString();
    }
    
    
    // Container

    public static HttpResponse listContainers(Map<String, String> params) throws Exception {
        return get("containers", "", params);
    }

    public static HttpResponse inspectContainer(String id, Map<String, String> params) throws Exception {
        return get("containers", id, params);
    }

    public static HttpResponse createContainer(String name, JSONObject body) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);

        String jsonBody =
            new ObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(body);

        return
            DockerController.post("containers/create", "",
            params,
            BodyPublishers.ofString(jsonBody),
            "application/json;charset=UTF-8"
            );
    }

    public static HttpResponse deleteContainer(String id, String force) throws Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("force", force);
        return
            delete("containers", id, params);
    }

    public static HttpResponse pruneContainers() throws Exception {
        return 
        post("/containers/prune", "",
        Client.getClient().noParameters(),
        Client.getClient().noBody(),
        "application/x-www-form-urlencoded");
    }

}
