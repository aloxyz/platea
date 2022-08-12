package alo;

import java.io.File;
import java.util.Arrays;

public class Container {
    public String name;
    public String endpoint;

    public void getJSON() {
        FileIO.wget(this.endpoint, "src/"+this.name);
    }

    public void build() {
        String[] cmd = new String[]{ "/bin/sh", "scripts/"+s.getName() };
        Runtime.getRuntime().exec(cmd);
    }
}
