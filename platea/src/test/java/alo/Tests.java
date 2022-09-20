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
    
    public String ImageName = "fileserver";

    @Test
    public void buildImageRemote() throws Exception {
        HttpResponse response = 
            DockerController.buildImageRemote(
                ImageName, 
                "instance_name", 
                "https://github.com/lcarnevale/docker-file-server.git#main");
        
        System.out.println(response.body().toString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void listImages() throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"service=platea\"]}");

        HttpResponse response =
            DockerController.listImages(params);

        System.out.println(
            response.body()
        );

        assertEquals(200, response.statusCode());
    }

    @Test
    public void createContainer() throws Exception {
        JSONObject body = new JSONObject();
        body = JSONController.fileToJsonObject("/home/alo/Documenti/platea/platea/src/main/java/alo/docker-file-server.json");

        HttpResponse response =
        DockerController.createContainer("", body);
        
        /*
        containerId =
        JSONController.stringToJSONObject(
            response.body().toString())
            .get("Id").toString();
        */

        System.out.println(response.body().toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void listContainers() throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"service=platea\"]}");

        HttpResponse response =
            DockerController.listContainers(params);

        System.out.println(
            response.body()
        );

        assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteContainer() throws Exception {
        HttpResponse response =
            DockerController.deleteContainer(containerId, "true");

        System.out.println(response.body().toString());
        assertEquals(204, response.statusCode());
    }
    
    @Test
    public void deleteImage() throws Exception {

        HttpResponse response =
            DockerController.deleteImage(ImageName, "true");

        System.out.println(response.body().toString());
        assertEquals(200, response.statusCode());
    }

}
