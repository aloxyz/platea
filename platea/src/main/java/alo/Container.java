package alo;

import java.util.LinkedHashMap;

public class Container {
    public String name;
    public String endpoint;

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getName() {
        return this.name;
    }

    public void InitializeFromLHM(LinkedHashMap<String,String> lhm) {
        //Initialize name and endpoint given a LinkedHashMap
        this.name = lhm.get("name").toString();
        this.endpoint = lhm.get("endpoint").toString();
    }

    public void getJSON() {
        FileIO.wget(this.endpoint, "src/"+this.name);
    }

    public void build() {
        try {
            String[] cmd = new String[]{ "/bin/sh", "scripts/"+this.name };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
