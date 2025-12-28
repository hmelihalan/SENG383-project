import javax.swing.*;
import java.awt.*;

public class KidTaskGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;

    public KidTaskGUI() {
        setTitle("KidTask - Task and Wish Management");
        setSize(1000, 700); // Matches the wide aspect ratio of the mockups
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize Login Screen
        loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "LOGIN");

        add(mainPanel);
    }

    // Method to switch to Dashboard after role selection
    public void showDashboard(String role, String username) {
        if (dashboardPanel != null) {
            mainPanel.remove(dashboardPanel);
        }
        dashboardPanel = new DashboardPanel(this, role, username);
        mainPanel.add(dashboardPanel, "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");
    }

    public void logout() {
        cardLayout.show(mainPanel, "LOGIN");
    }
}