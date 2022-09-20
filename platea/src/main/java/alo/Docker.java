package alo;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.util.Map;

public class Docker {

    public static HttpResponse get(String endpoint, String id, Map<String, String> params) throws Exception {
        System.out.println("GET: "+String.format("/%s/%s", endpoint, id));
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
        System.out.println("POST: "+String.format("/%s/%s", endpoint, id));
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
        System.out.println("DELETE: "+String.format("/%s/%s", endpoint, id));
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

}
