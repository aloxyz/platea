package alo;

public class App 

{
    public static void main( String[] args )
    {
        try {
            Mapper m = new Mapper();
            Container c = m.ContainerFromFile("/home/alo/Documenti/platea/platea/test.json");
            System.out.print(c);
            System.out.printf("\n%s\n%s\n%s\n%s\n", c.name, c.cpu, c.memory, c.volume);

            m.ContainerToFile(c, "tmp");
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
