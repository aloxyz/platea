package alo;

public class App 

{
    public static void main( String[] args )
    {
        try {
            Mapper m = new Mapper();
            FileIO.wget("https://gitlab.com/aloxyz/platea-configs/-/raw/main/lcarnevale.json", "json/lcarnevale.json");
            Instance i = m.InstanceFromFile("json/lcarnevale.json");
            
            System.out.println(i.getContainers().get(0).getName());
            System.out.println(i.getContainers().get(1).getName());            

            /*
            Database db = new Database("jdbc:postgresql://localhost/platea", "postgres", "postgres");
            
            if(db.connect() != null) {
                System.out.println("Connected to database "+ db +" successfully");
            }
            */
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
