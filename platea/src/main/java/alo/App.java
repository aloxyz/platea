package alo;

public class App 

{
    public static void main( String[] args )
    {
        try {
            Mapper m = new Mapper();
            FileIO.wget("https://gitlab.com/aloxyz/platea-configs/-/raw/main/lcarnevale.json", "json/lcarnevale.json");
            Instance i = m.InstanceFromFile("json/lcarnevale.json");
            
            i.build();
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
