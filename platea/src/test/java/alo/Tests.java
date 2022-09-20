package alo;

import static org.junit.Assert.assertEquals;

import java.net.http.HttpResponse;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
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

        JSONObject config = JSONController.fileToJsonObject(Paths.get("/home/alo/Documenti/platea/platea/sampleConfig.json").toString());
        JSONObject containers = (JSONObject)config.get("containers");
        JSONObject container = (JSONObject)containers.get("docker-file-server");
        JSONObject buildConfig = (JSONObject)container.get("config");

        HttpResponse buildImageRemoteResponse = Images.buildRemote(imageName, instanceName, uri);
        HttpResponse createContainerResponse = Containers.create(imageName, buildConfig);

        String containerId = Docker.getFromResponse(createContainerResponse, "Id");
        
        HttpResponse deleteContainerResponse = Containers.delete(containerId, "true");
        HttpResponse deleteImageResponse = Images.delete(imageName, "true");

        assertEquals(200, buildImageRemoteResponse.statusCode());
        assertEquals(201, createContainerResponse.statusCode());
        assertEquals(204, deleteContainerResponse.statusCode());
        assertEquals(200, deleteImageResponse.statusCode());
    }
}
