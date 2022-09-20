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


    public HttpResponse buildImageRemote(String imageName, String instance, String uri) throws Exception {
        HttpResponse response = 
            DockerController.buildImageRemote(
                "fileserver", 
                "instance_name", 
                "https://github.com/lcarnevale/docker-file-server.git#main");
        
        
        System.out.println(response.body().toString());
        assertEquals(200, response.statusCode());
        return response;
    }


    public HttpResponse listImages() throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"service=platea\"]}");

        HttpResponse response =
            DockerController.listImages(params);

        System.out.println(response.body());
        assertEquals(200, response.statusCode());
        return response;
    }    

    public HttpResponse deleteImage(String name) throws Exception {

        HttpResponse response =
            DockerController.deleteImage(name, "true");

        System.out.println(response.body().toString());
        assertEquals(200, response.statusCode());
        return response;
    }

    @Test
    public void dockerfileserver() throws Exception {
        String imageName = "fileserver";
        String instanceName = "instance_name";
        String uri = "https://github.com/lcarnevale/docker-file-server.git#main";

        String containerConfig = "/home/alo/Documenti/platea/platea/src/main/java/alo/docker-file-server.json";

        HttpResponse buildImageRemoteResponse = buildImageRemote(imageName, instanceName, uri);
        HttpResponse createContainerResponse = Containers.create(containerConfig);
        
        String containerId =
            JSONController.stringToJSONObject(
                createContainerResponse.body().toString())
                .get("Id").toString();
        
        HttpResponse deleteContainerResponse = Containers.delete(containerId);
        HttpResponse deleteImageResponse = deleteImage(imageName);

        assertEquals(200, buildImageRemoteResponse.statusCode());
        assertEquals(201, createContainerResponse.statusCode());
        assertEquals(204, deleteContainerResponse.statusCode());
        assertEquals(200, deleteImageResponse.statusCode());
    }
}
