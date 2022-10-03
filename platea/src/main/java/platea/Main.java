package platea;

import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    private static HashMap<String, String> helpMessages;
    private static JSONObject config;
    private static ArrayList<String> singleCommands = new ArrayList<>();
    private static ArrayList<String> argCommands = new ArrayList<>();
    private static Instance instance;


    private static void helpMessage() {
        System.out.println(
                ConsoleColors.BLUE_BOLD + "Usage:"+ ConsoleColors.RESET +"\nplatea <command> <instance>\n\n"+
                "Docker container provisioning tool\n\n"+
                ConsoleColors.BLUE_BOLD+
                "Name\t\tDescription"+
                ConsoleColors.RESET
            );
        for (String cmd : helpMessages.keySet()) {
            if(cmd.equals("instance")) {
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
        helpMessages.put("create", "Create the specified instance");
        helpMessages.put("start", "Start the specified instance");
        helpMessages.put("run", "Build and start the specified instance");
        helpMessages.put("stop", "Stop the specified instance");
        helpMessages.put("rm", "Remove the specified instance, along with belonging containers and images");
        


        singleCommands.add("help");
        singleCommands.add("fetch");
        singleCommands.add("ls");
        singleCommands.add("ps");
        
        argCommands.add("create");
        argCommands.add("build");
        argCommands.add("start");
        argCommands.add("run");
        argCommands.add("stop");
        argCommands.add("rm");

        if (args.length == 0) {
            helpMessage();
            return;
        }

        String command = args[0];

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
                    System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "Available instances\n" + ConsoleColors.RESET);
                    for (String i : Instances.listRemote()) {
                        System.out.println(i);
                    }
                    break;
    
                case "ps":
                    System.out.println(ConsoleColors.WHITE_BOLD + "Running instances\n" + ConsoleColors.RESET);
                    
                    ArrayList<String> running = Instances.listRunning();
                    if (running.size() <= 0) {
                        System.out.println(ConsoleColors.RED_BRIGHT + "No running platea instances" + ConsoleColors.RESET);
                    }
                    else {
                        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT +"Service\t\t\t\tInstance" + ConsoleColors.RESET);
                        for (String c : running) {
                            System.out.println(c);
                        }
                    }

                    break;
                }
            return;
        }

        if (args.length == 2 && argCommands.contains(command)) {
            String instanceName = args[1];
            boolean inDB = Database.getDatabase().getInstance(instanceName);

            if(!instanceName.isEmpty()) {
                String configPath = new File(
                    Config.getConfig().instancesPath() + instanceName + ".json")
                    .getAbsolutePath();

                    instance = new Instance(configPath);
            }

            try {
                if (!inDB && command.equals("create")) {   
                    instance.buildInstance();
                }

                else if (inDB) {
                    switch(command) {                    
                        case "start":
                            instance.startContainers();
                            break;

                        case "stop":
                            instance.stopContainers();
                            break;

                        case "rm":
                            instance.delete();
                            break;
                    }
                }
                else {
                    System.out.println("Instance is not initialized");
                }
            }
            catch (Exception e) {
                System.out.println("Error while referencing instance");
                e.printStackTrace();
            }
       
        }
    }
}


