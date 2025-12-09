import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private KidTaskGUI mainFrame;
    private JTextField nameField;

    public LoginPanel(KidTaskGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("KidTask", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(156, 39, 176)); // Purple brand color
        JLabel subtitle = new JLabel("Task and Wish Management for Kids", SwingConstants.CENTER);
        headerPanel.add(title);
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Role Cards Center
        JPanel rolesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 50));
        rolesPanel.setBackground(Color.WHITE);

        rolesPanel.add(createRoleCard("Child", "Complete tasks & earn", new Color(255, 152, 0))); // Orange
        rolesPanel.add(createRoleCard("Parent", "Manage & approve", new Color(100, 100, 255)));   // Blue/Purple
        rolesPanel.add(createRoleCard("Teacher", "Add school tasks", new Color(0, 200, 83)));     // Green

        add(rolesPanel, BorderLayout.CENTER);

        // Name Input at Bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(new JLabel("Your Name: "));
        nameField = new JTextField(15);
        bottomPanel.add(nameField);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createRoleCard(String role, String desc, Color color) {
        JButton btn = new JButton("<html><center><h2>" + role + "</h2>" + desc + "</center></html>");
        btn.setPreferredSize(new Dimension(200, 150));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        btn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) name = "User";
            mainFrame.showDashboard(role, name);
        });
        return btn;
    }
}