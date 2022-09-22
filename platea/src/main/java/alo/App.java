package alo;

import java.io.Console;
import java.util.HashMap;

public class App {
    static public HashMap<String, String> helpMessages;

    public static void main( String[] args) throws Exception {
        helpMessages = new HashMap<>();
        helpMessages.put("instance", "The instance name");
        helpMessages.put("fetch", "fetch instance files from the remote repository");
        helpMessages.put("ls", "List available instances from the remote repository");
        helpMessages.put("ps", "List running instances");
        helpMessages.put("build", "Build the specified instance");
        helpMessages.put("start", "Start the specified instance");
        helpMessages.put("run", "Build and start the specified instance");
        helpMessages.put("stop", "Stop the specified instance");
        helpMessages.put("rm", "Remove the specified instance, along with beloning containers and images");
        
        
        if (args.length == 0) {
            System.out.println(
                ConsoleColors.BLUE_BOLD + "Usage:"+ ConsoleColors.RESET +"\nplatea <command> <instance>\n\n"+
                ConsoleColors.BLUE_BOLD+
                "Name\t\tDescription"+
                ConsoleColors.RESET
            );
            for (String command : helpMessages.keySet()) {
                if(command == "instance") {
                    System.out.println(
                    ConsoleColors.BLUE+command+ConsoleColors.RESET+
                    "\t" + helpMessages.get(command)
                );
                } 
                else {
                    System.out.println(
                        ConsoleColors.BLUE + command + ConsoleColors.RESET + "\t\t" + helpMessages.get(command)
                    );
                }
            }
        }

        else if (args.length == 1) {
            System.out.println(
                helpMessages.get(args[0])
                );
            return;
        }

        else {
            switch(args[0]) {
                case "fetch":
                    // code block
                    break;
    
                case "ls":
                    // code block
                    break;
    
                case "ps":
                    // code block
                    break;
    
                case "build":
                    // code block
                    break;
    
                case "run":
                    // code block
                    break
                    ;
                case "start":
                    // code block
                    break;
                
                case "stop":
                    // code block
                    break;
                case "rm":
                    // code block
                    break;
    
                default:
                  // code block
            }
        }
        
    }
}