package alo;

import static org.junit.Assert.assertEquals;

import java.net.http.HttpResponse;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tests {

    @Test
    public void dockerfileserver() throws Exception {
        
        String instanceName = "instance_name";
        String imageName = "fileserver";
        String uri = "https://github.com/lcarnevale/docker-file-server.git#main";

        String containerConfig = "/home/alo/Documenti/platea/platea/src/main/java/alo/docker-file-server.json";

        HttpResponse buildImageRemoteResponse = Images.buildRemote(imageName, instanceName, uri);
        HttpResponse createContainerResponse = Containers.create(containerConfig);
        
        String containerId = DockerController.getFromResponse(createContainerResponse, "Id");
        
        HttpResponse deleteContainerResponse = Containers.delete(containerId);
        HttpResponse deleteImageResponse = Images.delete(imageName);

        assertEquals(200, buildImageRemoteResponse.statusCode());
        assertEquals(201, createContainerResponse.statusCode());
        assertEquals(204, deleteContainerResponse.statusCode());
        assertEquals(200, deleteImageResponse.statusCode());
    }
}
