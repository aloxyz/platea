package alo;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JSONController {
    
    public static JSONObject stringToJSONObject(String str) {
        return (JSONObject)JSONValue.parse(str);
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
