package alo;

public class Container {
    public String name;
    public String endpoint;

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
