package alo;


import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Tests {

    /*
    @Test
    public void commands() throws Exception {
        PlateaCommand cmd = new PlateaCommand();
        
        cmd.fetchInstances = true;
        cmd.listInstances = true;
        cmd.instanceName = "lcarnevale";
        cmd.runInstance = true;
        cmd.listRunningInstances = true;
        //cmd.stopInstance = true;
        //cmd.removeInstance = true;
        cmd.call();
    }
    
    @Test
    public void runInstance() throws Exception {
        Instances.run("/home/alo/Documenti/platea/platea/sampleConfig.json");
    }

    @Test
    public void stopInstance() throws Exception {
        Instances.stopContainers("lcarnevale");
    }

    @Test
    public void deleteInstance() throws Exception {
        Instances.delete("lcarnevale");
    }

    @Test
    public void buildInstance() throws Exception {
        Instances.buildImages("/home/alo/Documenti/platea/platea/sampleConfig.json");
        Instances.createContainers("/home/alo/Documenti/platea/platea/sampleConfig.json");



        System.out.println(Containers.list("lcarnevale", "").body().toString());
        System.out.println(Images.list("lcarnevale").body().toString());

    }

    @Test
    public void dockerfileserver() throws Exception {
        // SETUP
        
        JSONObject config = JSONController.fileToJsonObject(Paths.get("/home/alo/Documenti/platea/platea/sampleConfig.json").toString());
        JSONObject containers = (JSONObject)config.get("containers");
        JSONObject container = (JSONObject)containers.get("docker-file-server");
        JSONObject buildConfig = (JSONObject)container.get("config");

        String instanceName = config.get("instanceName").toString();
        String uri = container.get("endpoint").toString();

        //get name from image name
        String tmpImageName = buildConfig.get("Image").toString();
        String imageName = tmpImageName.substring(0, tmpImageName.lastIndexOf(":"));
        


        // START

        HttpResponse buildImageRemoteResponse = Images.buildRemote(imageName, instanceName, uri);
        HttpResponse createContainerResponse = Containers.create(imageName, instanceName, buildConfig);
        String containerId = Docker.getFromResponse(createContainerResponse, "Id");
        HttpResponse startContainerResponse = Containers.start(containerId);


        HttpResponse stopContainerResponse = Containers.stop(containerId);
        HttpResponse deleteContainerResponse = Containers.delete(containerId, "true");
        HttpResponse deleteImageResponse = Images.delete(imageName, "true");

        assertEquals(200, buildImageRemoteResponse.statusCode());
        assertEquals(201, createContainerResponse.statusCode());
        assertEquals(204, startContainerResponse.statusCode());
        assertEquals(204, stopContainerResponse.statusCode());
        assertEquals(204, deleteContainerResponse.statusCode());
        assertEquals(200, deleteImageResponse.statusCode());
    }
    */
}
