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
            ProcessBuilder builder = new ProcessBuilder();
            builder.inheritIO();
            String[] cmd = {"/bin/sh", "scripts/"+this.name+".sh"};
            builder.command(cmd);
            Process p = builder.start();
            p.waitFor();
            System.out.println(p+" exited with value "+p.exitValue());
            /*
            String[] cmd = new String[]{ "/bin/sh", "scripts/"+this.name+".sh" };
            Runtime.getRuntime().exec(cmd);
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
