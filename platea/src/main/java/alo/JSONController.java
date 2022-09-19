package alo;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONController {
    

    public static ArrayList<JSONObject> JSONArrayToList(JSONArray jsonArray) {
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject o = (JSONObject)jsonArray.get(i);
            list.add(o);
        }
        return list;
    }
}
