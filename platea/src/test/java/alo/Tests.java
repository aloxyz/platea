package alo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.net.http.HttpResponse;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.junit.Test;

public class Tests {
    
    @Test
    public void buildImageRemote() throws Exception {
        HttpResponse response = 
            DockerController.buildImageRemote(
                "fileserver", 
                "instance_name", 
                "https://github.com/lcarnevale/docker-file-server.git#main");
        
        System.out.println(response.body().toString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteImage() throws Exception {
        HttpResponse response =
            DockerController.deleteImage("42178f2d138c019afe09a26d74fcfc299d076d115fca2b9775fa460a4caca3a9");

        System.out.println(response.body().toString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void listImages() throws Exception {
        HashMap<String,String> params = new HashMap<>();
        params.put("filters", "{\"label\":[\"service\"]}");

        HttpResponse response =
            DockerController.listImages(params);

        System.out.println(
            response.body()
        );

        assertEquals(200, response.statusCode());
    }

    @Test
    public void CreateContainer() throws Exception {
        JSONObject body = new JSONObject();
        body.put("image", "fileserver");

        HttpResponse response =
        DockerController.createContainer("mio_fileserver", body);
        
        System.out.println(response.body().toString());
        assertEquals(201, response.statusCode());
    }
}
