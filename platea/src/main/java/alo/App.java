package alo;


public class App

{
    public static void main( String[] args) throws Exception
    {    

        Instances.run("/home/alo/Documenti/platea/platea/sampleConfig.json");
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