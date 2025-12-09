import javax.swing.*;
import java.awt.*;
import java.util.Collections;

public class DashboardPanel extends JPanel {
    private KidTaskGUI mainFrame;
    private String role;
    private String username;
    
    // UI Components
    private JPanel taskListPanel;
    private JPanel wishListPanel;
    private JLabel pointsLabel;
    private JLabel levelLabel;

    public DashboardPanel(KidTaskGUI mainFrame, String role, String username) {
        this.mainFrame = mainFrame;
        this.role = role;
        this.username = username;
        
        setLayout(new BorderLayout());
        
        // 1. Top Bar (Welcome + Logout)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(getRoleColor());
        topBar.setPreferredSize(new Dimension(1000, 80));
        
        JLabel welcomeLabel = new JLabel("  Welcome, " + username + " (" + role + ")!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> mainFrame.logout());
        
        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // 2. Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        // --- TASKS TAB ---
        JPanel tasksTab = new JPanel(new BorderLayout());
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        JScrollPane taskScroll = new JScrollPane(taskListPanel);
        
        // Add Task Button (Only for Parent/Teacher)
        if (!role.equals("Child")) {
            JButton addTaskBtn = new JButton("+ Add New Task");
            addTaskBtn.setBackground(Color.BLACK);
            addTaskBtn.setForeground(Color.WHITE);
            addTaskBtn.addActionListener(e -> showAddTaskDialog());
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.add(addTaskBtn);
            tasksTab.add(btnPanel, BorderLayout.NORTH);
        }
        
        tasksTab.add(taskScroll, BorderLayout.CENTER);
        tabbedPane.addTab("Tasks", tasksTab);

        // --- WISHES TAB ---
        JPanel wishesTab = new JPanel(new BorderLayout());
        
        // Stats Header for Child (Points/Level)
        if (role.equals("Child")) {
            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
            statsPanel.setBackground(new Color(240, 240, 250));
            pointsLabel = new JLabel("Points: " + Child.pointsTotal);
            levelLabel = new JLabel("Level: " + Child.level);
            pointsLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            levelLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            statsPanel.add(pointsLabel);
            statsPanel.add(levelLabel);
            wishesTab.add(statsPanel, BorderLayout.NORTH);
        }

        wishListPanel = new JPanel();
        wishListPanel.setLayout(new BoxLayout(wishListPanel, BoxLayout.Y_AXIS));
        JScrollPane wishScroll = new JScrollPane(wishListPanel);
        wishesTab.add(wishScroll, BorderLayout.CENTER);
        
        // Add Wish Button (Only Child)
        if (role.equals("Child")) {
            JButton addWishBtn = new JButton("+ Add Wish");
            addWishBtn.setBackground(new Color(156, 39, 176));
            addWishBtn.setForeground(Color.WHITE);
            addWishBtn.addActionListener(e -> showAddWishDialog());
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.add(addWishBtn);
            wishesTab.add(btnPanel, BorderLayout.SOUTH);
        }
        
        tabbedPane.addTab("Wishes", wishesTab);

        // --- PROGRESS TAB ---
        // (Simplified for this version, just shows stats)
        JPanel progressTab = new JPanel();
        progressTab.add(new JLabel("Detailed Charts & Graphs coming soon..."));
        tabbedPane.addTab("Progress", progressTab);

        add(tabbedPane, BorderLayout.CENTER);
        
        // Load Data
        refreshData();
    }
    
    private Color getRoleColor() {
        if (role.equals("Child")) return new Color(255, 152, 0); // Orange
        if (role.equals("Teacher")) return new Color(0, 200, 83); // Green
        return new Color(100, 100, 255); // Parent Blue
    }

    // --- REFRESH LOGIC ---
    private void refreshData() {
        // Clear Lists
        taskListPanel.removeAll();
        wishListPanel.removeAll();
        
        // Update Stats
        if(pointsLabel != null) pointsLabel.setText("Points: " + Child.pointsTotal);
        if(levelLabel != null) levelLabel.setText("Level: " + Child.level);

        // 1. Populate Tasks
        for (Task t : TaskManager.tasks.values()) {
            taskListPanel.add(createTaskCard(t));
            taskListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // 2. Populate Wishes
        for (Wish w : WishManager.wishes.values()) {
            wishListPanel.add(createWishCard(w));
            wishListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        taskListPanel.revalidate();
        taskListPanel.repaint();
        wishListPanel.revalidate();
        wishListPanel.repaint();
    }

    // --- CARD CREATION HELPERS ---
    
    // Creates a visual card for a single task
    private JPanel createTaskCard(Task t) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(900, 80));
        card.setPreferredSize(new Dimension(900, 80));

        // Left: Title and Desc
        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel title = new JLabel(" " + t.title + " (" + t.pointVal + " pts)");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel desc = new JLabel(" " + t.desc + " | Due: " + t.deadlineDate);
        left.add(title);
        left.add(desc);
        card.add(left, BorderLayout.CENTER);

        // Right: Action Button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        
        JLabel statusLbl = new JLabel(t.status.toString() + "  ");
        right.add(statusLbl);

        if (role.equals("Child") && t.status == Task.TaskStatus.PENDING) {
            JButton completeBtn = new JButton("Mark Done");
            completeBtn.setBackground(Color.GREEN);
            completeBtn.addActionListener(e -> {
                // Bridge to backend logic
                String cmd = "TASK_DONE " + t.taskID;
                TaskManager.completeTask(cmd);
                refreshData();
            });
            right.add(completeBtn);
        } else if ((role.equals("Parent") || role.equals("Teacher")) && t.status == Task.TaskStatus.COMPLETED) {
            JButton approveBtn = new JButton("Approve");
            approveBtn.setBackground(Color.CYAN);
            approveBtn.addActionListener(e -> {
                // Show Rating Dialog
                String ratingStr = JOptionPane.showInputDialog("Rate this task (1-5):");
                if (ratingStr != null) {
                    // Bridge to backend logic
                    String cmd = "TASK_CHECKED " + t.taskID + " " + ratingStr;
                    TaskManager.approveTask(cmd);
                    refreshData();
                }
            });
            right.add(approveBtn);
        }

        card.add(right, BorderLayout.EAST);
        return card;
    }

    // Creates a visual card for a wish
    private JPanel createWishCard(Wish w) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(900, 80));

        // Left
        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel title = new JLabel(" " + w.title + " (Lvl Req: " + w.requiredLvl + ")");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        left.add(title);
        left.add(new JLabel(" " + w.desc + " | " + w.status));
        card.add(left, BorderLayout.CENTER);

        // Right
        JPanel right = new JPanel();
        right.setOpaque(false);
        if ((role.equals("Parent")) && w.status == Wish.WishStatus.PENDING) {
            JButton approveBtn = new JButton("Approve");
            approveBtn.setBackground(Color.GREEN);
            approveBtn.addActionListener(e -> {
                // Bridge to backend
                // WISH_CHECKED [ID] [STATUS] [LEVEL]
                String cmd = "WISH_CHECKED " + w.wishID + " APPROVED " + w.requiredLvl; 
                WishManager.approveWish(cmd);
                refreshData();
            });
            JButton rejectBtn = new JButton("Reject");
            rejectBtn.setBackground(Color.RED);
            rejectBtn.setForeground(Color.WHITE);
            rejectBtn.addActionListener(e -> {
                String cmd = "WISH_CHECKED " + w.wishID + " REJECTED";
                WishManager.approveWish(cmd);
                refreshData();
            });
            right.add(approveBtn);
            right.add(rejectBtn);
        }
        card.add(right, BorderLayout.EAST);
        return card;
    }

    // --- DIALOGS ---
    
    private void showAddTaskDialog() {
        // Simple form mimicking
        JTextField titleF = new JTextField();
        JTextField descF = new JTextField();
        JTextField dateF = new JTextField("2025-01-01");
        JTextField timeF = new JTextField("12:00");
        JTextField pointsF = new JTextField("10");
        
        Object[] message = {
            "Title:", titleF,
            "Description:", descF,
            "Due Date (yyyy-MM-dd):", dateF,
            "Due Time (HH:mm):", timeF,
            "Points:", pointsF
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add New Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            // Generate ID (Quick hack: max ID + 1)
            int newId = 100 + TaskManager.tasks.size() + 1;
            String assigner = role.equals("Teacher") ? "T" : "F";
            
            // Construct Command String: ADD_TASK1 T 101 "Title" "Desc" Date Time Points
            String cmd = String.format("ADD_TASK1 %s %d \"%s\" \"%s\" %s %s %s", 
                assigner, newId, titleF.getText(), descF.getText(), dateF.getText(), timeF.getText(), pointsF.getText());
            
            TaskManager.addTask(cmd);
            refreshData();
        }
    }

    private void showAddWishDialog() {
        // Simple form mimicking
        JTextField titleF = new JTextField();
        JTextField descF = new JTextField();
        
        Object[] message = {
            "Wish Title:", titleF,
            "Description (Price/Details):", descF
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add New Wish", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int newId = 100 + WishManager.wishes.size() + 1;
            // Construct Command: ADD_WISH1 W105 "Title" "Desc"
            String cmd = String.format("ADD_WISH1 W%d \"%s\" \"%s\"", newId, titleF.getText(), descF.getText());
            WishManager.addWish(cmd);
            refreshData();
        }
    }
}