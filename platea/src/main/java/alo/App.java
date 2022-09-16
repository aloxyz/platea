package alo;

public class App

{
    public static void main( String[] args) throws Exception
    {
        
        Client.getClient().connect();
        
        Instance i = Orchestrator.newInstance("lcarnevale.json");
        
        //i.runContainers();

        Orchestrator.fetchRemoteInstances();

        i.fetchContainersSource();
        i.buildContainers();
        i.getContainer(0).getID();
        i.deleteContainers();

        System.out.println(Orchestrator.listPlateaInstances());
        
        //i.deleteContainers();

        System.out.println(Orchestrator.listPlateaInstances());
        
        /*
        
        PlateaCommand cmd = new PlateaCommand();
        
        cmd.listInstances = true;
        cmd.instanceFile = new File("/home/alo/.config/platea/configs/lcarnevale.json");
        cmd.runInstance = true;
        
        cmd.call();

        Mapper m = new Mapper();
        FileIO.wget("https://gitlab.com/aloxyz/platea-configs/-/raw/main/lcarnevale.json", "json/lcarnevale.json");
        Instance i = m.InstanceFromFile("json/lcarnevale.json");
        
        i.build();
        
        Database db = new Database("jdbc:postgresql://localhost/platea", "postgres", "postgres");
        
        if(db.connect() != null) {
            System.out.println("Connected to database "+ db +" successfully");
        }
        */
    }
}
