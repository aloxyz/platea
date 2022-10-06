package platea;

import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.util.Map;

public class Docker {

    public static HttpResponse get(String endpoint, String id, Map<String, String> params) {
        //System.out.println("GET: "+String.format("/%s/%s", endpoint, id));
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

    public static HttpResponse post(String endpoint, String id, Map<String, String> parameters, BodyPublisher body, String headers) {
        //System.out.println("POST: "+String.format("/%s/%s", endpoint, id));
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

    public static HttpResponse delete(String endpoint, String id, Map<String, String> parameters) {
        //System.out.println("DELETE: "+String.format("/%s/%s", endpoint, id));
        return 
            Client.getClient()
            .deleteResource(
                String.format("/%s/%s", endpoint, id),
                parameters);
    }
}
