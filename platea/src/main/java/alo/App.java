package alo;

public class App 

{
    public static void main( String[] args )
    {
        try {
            Mapper m = new Mapper();
            Instance c = m.InstanceFromFile("/home/alo/Documenti/platea-configs/test.json");
            System.out.print(c);

            FileIO.wget("https://gitlab.com/aloxyz/platea-configs/-/raw/main/lcarnevale.json", "../json/");

            Database db = new Database("jdbc:postgresql://localhost/platea", "postgres", "postgres");
            
            if(db.connect() != null) {
                System.out.println("Connected to database "+ db +" successfully");
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
