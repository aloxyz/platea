package alo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class App {
    static private HashMap<String, String> helpMessages;
    static private String command = "";
    static private String instanceName = "";
    static private String configPath = "";
    static private final ArrayList<String> singleCommands = new ArrayList<>();
    static private final ArrayList<String> argCommands = new ArrayList<>();

    private static void helpMessage() {
        System.out.println(
                ConsoleColors.BLUE_BOLD + "Usage:"+ ConsoleColors.RESET +"\nplatea <command> <instance>\n\n"+
                "Docker container provisioning tool\n\n"+
                ConsoleColors.BLUE_BOLD+
                "Name\t\tDescription"+
                ConsoleColors.RESET
            );
        for (String cmd : helpMessages.keySet()) {
            if(cmd == "instance") {
                System.out.println(
                ConsoleColors.BLUE+cmd+ConsoleColors.RESET+
                "\t" + helpMessages.get(cmd)
            );
            } 
            else {
                System.out.println(
                    ConsoleColors.BLUE + cmd + ConsoleColors.RESET + "\t\t" + helpMessages.get(cmd)
                );
            }
        }
    }

    public static void main( String[] args) throws Exception {
        
        helpMessages = new HashMap<>();
        helpMessages.put("instance", "The instance name");
        helpMessages.put("fetch", "fetch instance files from the remote repository");
        helpMessages.put("help", "Display this help message");
        helpMessages.put("ls", "List available instances from the remote repository");
        helpMessages.put("ps", "List running instances");
        helpMessages.put("build", "Build the specified instance");
        helpMessages.put("start", "Start the specified instance");
        helpMessages.put("run", "Build and start the specified instance");
        helpMessages.put("stop", "Stop the specified instance");
        helpMessages.put("rm", "Remove the specified instance, along with belonging containers and images");
        


        singleCommands.add("help");
        singleCommands.add("fetch");
        singleCommands.add("ls");
        singleCommands.add("ps");
        
        argCommands.add("build");
        argCommands.add("start");
        argCommands.add("run");
        argCommands.add("stop");
        argCommands.add("rm");

        if (args.length == 0) {
            helpMessage();
            return;
        }

        command = args[0];

        if (! (singleCommands.contains(command) || argCommands.contains(command))) {
            System.out.println("Invalid command: " + command);
        }

        if (args.length == 1 && argCommands.contains(command)) {
            
            System.out.println(
                "Usage:\n\n"+
                "platea " + command + " <instance>\n\n"+
                "Description: " + helpMessages.get(command)
                );

            return;
        }

        if (singleCommands.contains(command)) {
            
            switch(command) { 
                case "help":
                    helpMessage();
                    break;

                case "fetch":
                    Instances.fetchRemote();
                    break;
    
                case "ls":
                    System.out.println(
                            Instances.listRemote()
                    );
                    break;
    
                case "ps":
                    System.out.println(
                            Instances.listRunning()
                    );
                    break;
                }
            return;
        }

        if (args.length == 2 && argCommands.contains(command)) {
            instanceName = args[1];

            if(!instanceName.isEmpty()) {
                configPath = new File(
                    Config.getConfig().instancesPath() + instanceName + ".json")
                    .getAbsolutePath();
            }
            
            switch(command) {    
                
                case "build":
                    Instances.buildImages(configPath);
                    Instances.createContainers(configPath);
                    break;
    
                case "start":
                    Instances.startContainers(instanceName);
                    break;

                case "run":
                    Instances.run(configPath);
                    break;
                
                case "stop":
                    Instances.stopContainers(instanceName);
                    break;
                case "rm":
                    Instances.deleteContainers(instanceName);
                    break;
            }
        }
    }
}