package alo;

import java.net.http.HttpResponse;

public class DockerController {

    public static HttpResponse getContainers(String id) throws Exception {
        if (!id.isEmpty()) {
            return
                Client.getClient()
                .getResource(
                    "/containers/"+ id +"/json",
                    Client.getClient().noParameters());
        } 
        
        else {
            return
                Client.getClient()
                .getResource(
                    "/containers/json",
                    Client.getClient().noParameters());
        }

            
    }
}
