import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandProcessor {
    
    public static void commandReader(String commPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(commPath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                processCommand(line);
            }
        }
    }

    private static void processCommand(String commandLine) {
        // Split command type and arguments
        String[] parts = commandLine.split("\\s+", 2);
        String commandType = parts[0];
       

        switch (commandType) {
            case "ADD_TASK1":  
            case "ADD_TASK2":
                TaskManager.addTask(commandLine);
                break;
            case "TASK_DONE":
                TaskManager.completeTask(commandLine);
                break;
            case "TASK_CHECKED":
                TaskManager.approveTask(commandLine);
                break;
            case "LIST_ALL_TASKS": 
                TaskManager.listAllTasks();
                break;
            case "ADD_WISH1":  // Add wish commands
            case "ADD_WISH2":
             WishManager.addWish(commandLine);
             break;
            case "WISH_CHECKED":
             WishManager.approveWish(commandLine);
             break;
            case "LIST_ALL_WISHES":
             WishManager.listAllWishes();
             break;
            case "ADD_BUDGET_COIN":
                Child.addBudgetCoin(commandLine);
                break;
            case "PRINT_BUDGET": // Added PRINT_BUDGET command
                Child.printBudget();
                break;
               case "PRINT_STATUS": // Added PRINT_STATUS command
                Child.printStatus();
                break;
            default:
                System.out.println("Unknown command: " + commandLine);
        }
    }
}
	
/*You will create a command reader that uses the filepath for commands. It will read line by line and process each command as a string
 * It will only take consideration of the command type( ADD_TASK1 etc). After which it will decide on which one of the command managers it 
 * will send the whole command as string to.
 * */
	/* I'm planning to store the task and wish lists as a list data structure in the memory. Hashmap,linkedlist,array etc. That way
	 * handling wishes and tasks will be easier*/

