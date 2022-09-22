package alo;


public class App

{
    public static void main( String[] args) throws Exception
    {    

        PlateaCommand cmd = new PlateaCommand();
        
        cmd.listInstances = true;
        cmd.instanceName = "lcarnevale";
        cmd.runInstance = true;
        cmd.stopInstance = true;
        
        cmd.call();

    }
}