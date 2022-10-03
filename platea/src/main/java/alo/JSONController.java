package alo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

public class JSONController {
    
    public static JSONObject stringToJSONObject(String str) {
        return (JSONObject)JSONValue.parse(str);
    }

    public static JSONObject fileToJsonObject(String path) throws Exception {
        return
            (JSONObject) new JSONParser().parse(Files.readString(Paths.get(path)));



    }

    public static ArrayList<JSONObject> JSONArrayToList(JSONArray jsonArray) {
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject o = (JSONObject)jsonArray.get(i);
            list.add(o);
        }
        return list;
    }
}
