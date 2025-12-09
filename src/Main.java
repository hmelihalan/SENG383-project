import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Load existing data from files
        WishManager.loadWishesFromFile();
        TaskManager.loadTasksFromFile();
        
        // Launch GUI
        SwingUtilities.invokeLater(() -> {
            new KidTaskGUI().setVisible(true);
        });
    }
}
/* DO NOT FORGET TO ADD A recordToFile METHOD. It will be called at the end of
 *  each method that modifies or adds a task and it'll take the current tasks ID.
 *  Inside the file recorder method the method will get the needed task by 
 *  it's id from the hashmap and record that task into the file
 *  
 *  a similar method will also be called in the main method at startup to record the task and wish files into the hashmap(memory)
 *  
 *  
 *   */ 
