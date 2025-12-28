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

