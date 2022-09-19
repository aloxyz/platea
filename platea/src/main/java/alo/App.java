package alo;


import java.net.http.HttpRequest.BodyPublishers;
import java.nio.file.Paths;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class App

{
    public static void main( String[] args) throws Exception
    {
        System.out.println(
            DockerController.listContainers()
            .body().toString()
        );         

        
        /*String imagesJSON =
            DockerController
            .get(
                "images",
                "")
                .body().toString();

        JSONArray jsonDocument = (JSONArray)JSONValue.parse(imagesJSON);

        for(JSONObject o : JSONController.JSONArrayToList(jsonDocument)) {
            System.out.println(o.get("Id"));
        } */
    }
}