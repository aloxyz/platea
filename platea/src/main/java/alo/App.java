package alo;

public class App 

{
    public static void main( String[] args )
    {
        try {
            Mapper m = new Mapper();
            Container c = m.ContainerFromFile("/home/alo/Documenti/platea-configs/test.json");
            System.out.print(c);
            System.out.printf("\n%s\n%s\n%s\n%s\n", c.name, c.cpu, c.memory, c.volume);

            Container cc = m.ContainerFromURL("https://gitlab.com/aloxyz/platea-configs/-/raw/main/test.json");
            System.out.print(cc);
            System.out.printf("\n%s\n%s\n%s\n%s\n", cc.name, cc.cpu, cc.memory, cc.volume);

            m.ContainerToFile(c, "tmp");

            Database db = new Database("jdbc:postgresql://localhost/platea", "postgres", "postgres");
            
            if(db.connect() != null) {
                System.out.println("Connected to database "+ db +" successfully");
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
