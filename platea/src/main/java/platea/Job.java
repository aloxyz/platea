package platea;

import org.json.JSONObject;

public class Job {
    private JSONObject config;
    private String name;
    Job(JSONObject config, String name) {
        this.config = config;
        this.name = name;
    }
}
