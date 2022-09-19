package alo;

import org.json.simple.JSONObject;
import org.junit.Test;

public class Tests {
    
    @Test
    public void buildImageRemote() throws Exception {
        DockerController.buildImageRemote("ciao", "https://github.com/lcarnevale/docker-file-server.git#main");
        
        System.out.println(
            DockerController.listImages()
            .body().toString()
        );
                
        DockerController.pruneImages();
    }

    @Test
    public void CreateContainer() throws Exception {
        JSONObject body = new JSONObject();

        DockerController.createContainer("ciao", body);

        System.out.println(
            DockerController.listContainers()
            .body().toString()
        );
        
        DockerController.pruneContainers();
    }
}
