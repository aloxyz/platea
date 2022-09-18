package alo;

import java.net.http.HttpResponse;

public class DockerController {

    public static HttpResponse get(String object, String id) throws Exception {
        if (!id.isEmpty()) {
            return
                Client.getClient()
                .getResource(
                    String.format("/%s/%s/json", object, id),
                    Client.getClient().noParameters());
        } 
        
        else {
            return
                Client.getClient()
                .getResource(
                    String.format("/%s/json", object),
                    Client.getClient().noParameters());
        }   
    }
}
