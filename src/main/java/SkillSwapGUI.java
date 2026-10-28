import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SkillSwapGUI extends JFrame {

    private final SkillSwapManager manager = new SkillSwapManager();

    private final Color navy = new Color(29, 78, 137);
    private final Color blue = new Color(37, 99, 235);

    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField additionalField = new JTextField(20);

    private final JComboBox<String> teachCombo = createSkillCombo();
    private final JComboBox<String> learnCombo = createSkillCombo();
    private final JComboBox<String> searchCombo = createSkillCombo();

    private final JTextField deleteIdField = new JTextField(8);

    private final DefaultTableModel studentModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Email", "Teach Skill",
                    "Learn Skill", "Additional Skills"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel searchModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Email",
                    "Teach Skill", "Learn Skill"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel matchModel = new DefaultTableModel(
            new String[]{"Student 1", "Teaches", "Learns",
                    "Student 2", "Teaches", "Learns"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JLabel studentCount;
    private JLabel matchCount;
    private JLabel statusLabel;

    public SkillSwapGUI() {

        setTitle("Skill Swap Platform");
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createNavigation(), BorderLayout.WEST);
        add(createPages(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        refreshDashboard();
    }

    // Predefined skills, but users can also type their own.
    private JComboBox<String> createSkillCombo() {

        JComboBox<String> combo = new JComboBox<>(new String[]{
                "Java", "Python", "C++", "JavaScript",
                "HTML", "CSS", "Web Design", "Graphic Design",
                "Photoshop", "Data Science", "Machine Learning",
                "SQL", "Public Speaking", "Video Editing"
        });

        combo.setEditable(true);
        return combo;
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(navy);
        header.setBorder(
                BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("Skill Swap Platform");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel subtitle = new JLabel(
                "Learn together. Grow together.");
        subtitle.setForeground(Color.WHITE);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 5));
        text.setOpaque(false);
        text.add(title);
        text.add(subtitle);

        header.add(text, BorderLayout.WEST);
        return header;
    }

    private JPanel createNavigation() {

        JPanel nav = new JPanel();
        nav.setLayout(new GridLayout(6, 1, 5, 8));
        nav.setBackground(new Color(239, 244, 250));
        nav.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 15, 10));

        String[] pages = {
                "Home", "Register Student", "View All Students",
                "Search Skill", "Find Matches", "Delete Student"
        };

        for (String page : pages) {
            JButton button = new JButton(page);
            button.setFocusPainted(false);
            button.addActionListener(e -> showPage(page));
            nav.add(button);
        }

        nav.setPreferredSize(new Dimension(190, 0));
        return nav;
    }

    private JTabbedPane createPages() {

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Home", createHomePage());
        tabs.addTab("Register", createRegisterPage());
        tabs.addTab("Students", createStudentPage());
        tabs.addTab("Search", createSearchPage());
        tabs.addTab("Matches", createMatchPage());
        tabs.addTab("Delete", createDeletePage());

        // Navigation buttons select the corresponding tab.
        navigationTabs = tabs;

        return tabs;
    }

    private JTabbedPane navigationTabs;

    private void showPage(String page) {

        if (navigationTabs == null) {
            return;
        }

        String tab = switch (page) {
            case "Register Student" -> "Register";
            case "View All Students" -> "Students";
            case "Search Skill" -> "Search";
            case "Find Matches" -> "Matches";
            case "Delete Student" -> "Delete";
            default -> "Home";
        };

        navigationTabs.setSelectedIndex(switch (tab) {
            case "Register" -> 1;
            case "Students" -> 2;
            case "Search" -> 3;
            case "Matches" -> 4;
            case "Delete" -> 5;
            default -> 0;
        });

        if (page.equals("View All Students")) {
            refreshStudents();
        } else if (page.equals("Find Matches")) {
            refreshMatches();
        } else if (page.equals("Home")) {
            refreshDashboard();
        }
    }

    private JPanel createHomePage() {

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(35, 30, 35, 30));

        JLabel welcome = new JLabel(
                "Welcome to Skill Swap Platform",
                SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 23));

        studentCount = new JLabel(
                "Total Students: 0", SwingConstants.CENTER);
        matchCount = new JLabel(
                "Potential Matches: 0", SwingConstants.CENTER);

        panel.add(welcome);
        panel.add(studentCount);
        panel.add(matchCount);

        JButton register = new JButton("Register Student");
        register.addActionListener(e -> showPage("Register Student"));

        JButton view = new JButton("View All Students");
        view.addActionListener(e -> showPage("View All Students"));

        panel.add(register);
        panel.add(view);

        return panel;
    }

    private JPanel createRegisterPage() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(9, 8, 9, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(panel, c, 0, "Name:", nameField);
        addFormRow(panel, c, 1, "Email:", emailField);
        addFormRow(panel, c, 2, "Skill to Teach:", teachCombo);
        addFormRow(panel, c, 3, "Skill to Learn:", learnCombo);
        addFormRow(panel, c, 4,
                "Additional Skills:", additionalField);

        JLabel hint = new JLabel(
                "Choose a skill or type your own.");
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        panel.add(hint, c);

        JButton register = primaryButton("Register Student");
        register.addActionListener(e -> registerStudent());

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel();
        buttons.add(register);
        buttons.add(clear);

        c.gridy = 6;
        panel.add(buttons, c);

        return panel;
    }

    private void addFormRow(
            JPanel panel, GridBagConstraints c,
            int row, String label, Component field) {

        c.gridy = row;
        c.gridwidth = 1;
        c.gridx = 0;
        panel.add(new JLabel(label), c);

        c.gridx = 1;
        field.setPreferredSize(new Dimension(280, 30));
        panel.add(field, c);
    }

    private JPanel createStudentPage() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTable table = new JTable(studentModel);
        styleTable(table);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refresh = primaryButton("Refresh Students");
        refresh.addActionListener(e -> refreshStudents());

        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSearchPage() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Skill to search:"));
        top.add(searchCombo);

        JButton search = primaryButton("Search");
        search.addActionListener(e -> searchStudents());
        top.add(search);

        JTable table = new JTable(searchModel);
        styleTable(table);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMatchPage() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTable table = new JTable(matchModel);
        styleTable(table);

        JButton find = primaryButton("Find Matches");
        find.addActionListener(e -> refreshMatches());

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(find, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDeletePage() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Student ID:"), c);

        c.gridx = 1;
        panel.add(deleteIdField, c);

        JButton delete = new JButton("Delete Student");
        delete.setBackground(new Color(190, 45, 45));
        delete.setForeground(Color.WHITE);
        delete.addActionListener(e -> deleteStudent());

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        panel.add(delete, c);

        JLabel hint = new JLabel(
                "Find the ID in the Students tab.");
        c.gridy = 2;
        panel.add(hint, c);

        return panel;
    }

    private JPanel createStatusBar() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statusLabel = new JLabel("Ready");
        panel.add(statusLabel, BorderLayout.WEST);

        return panel;
    }

    private JButton primaryButton(String text) {

        JButton button = new JButton(text);
        button.setBackground(blue);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        return button;
    }

    private void styleTable(JTable table) {

        table.setRowHeight(26);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
    }

    private String selectedSkill(JComboBox<String> combo) {

        Object value = combo.getEditor().getItem();

        return value == null ? "" : value.toString().trim();
    }

    private void registerStudent() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String teach = selectedSkill(teachCombo);
        String learn = selectedSkill(learnCombo);
        String additional = additionalField.getText().trim();

        if (name.isEmpty() || email.isEmpty()
                || teach.isEmpty() || learn.isEmpty()) {

            showError("Please fill in all required fields.");
            return;
        }

        if (!email.matches("^[^\\\\s@]+@[^\\\\s@]+\\\\.[^\\\\s@]+$")) {
            showError("Enter a valid email address.");
            return;
        }

        if (teach.equalsIgnoreCase(learn)) {
            showError("Teaching and learning skills must be different.");
            return;
        }

        try {
            manager.registerStudent(
                    name, email, teach, learn, additional);

            JOptionPane.showMessageDialog(
                    this, "Student registered successfully.");

            clearForm();
            refreshStudents();
            refreshDashboard();

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                showError("This email is already registered.");
            } else {
                showDatabaseError(ex);
            }
        }
    }

    private void clearForm() {

        nameField.setText("");
        emailField.setText("");
        teachCombo.setSelectedIndex(0);
        learnCombo.setSelectedIndex(1);
        additionalField.setText("");
    }

    private void refreshStudents() {

        try {
            List<SkillStudent> students = manager.getAllStudents();

            studentModel.setRowCount(0);

            for (SkillStudent s : students) {
                studentModel.addRow(new Object[]{
                        s.getId(),
                        s.getName(),
                        s.getEmail(),
                        s.getTeachSkill(),
                        s.getLearnSkill(),
                        s.getAdditionalSkills()
                });
            }

            setStatus("Students loaded: " + students.size());

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void searchStudents() {

        String skill = selectedSkill(searchCombo);

        if (skill.isEmpty()) {
            showError("Enter or select a skill.");
            return;
        }

        try {
            List<SkillStudent> students =
                    manager.searchBySkill(skill);

            searchModel.setRowCount(0);

            for (SkillStudent s : students) {
                searchModel.addRow(new Object[]{
                        s.getId(),
                        s.getName(),
                        s.getEmail(),
                        s.getTeachSkill(),
                        s.getLearnSkill()
                });
            }

            setStatus("Students found: " + students.size());

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void refreshMatches() {

        try {
            List<SkillStudent[]> matches = manager.findMatches();

            matchModel.setRowCount(0);

            for (SkillStudent[] pair : matches) {

                SkillStudent first = pair[0];
                SkillStudent second = pair[1];

                matchModel.addRow(new Object[]{
                        first.getName(),
                        first.getTeachSkill(),
                        first.getLearnSkill(),
                        second.getName(),
                        second.getTeachSkill(),
                        second.getLearnSkill()
                });
            }

            setStatus("Potential matches: " + matches.size());

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void deleteStudent() {

        String value = deleteIdField.getText().trim();

        if (value.isEmpty()) {
            showError("Enter the student ID.");
            return;
        }

        try {
            int id = Integer.parseInt(value);

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Delete student with ID " + id + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            manager.deleteStudent(id);

            JOptionPane.showMessageDialog(
                    this, "Student deleted successfully.");

            deleteIdField.setText("");

            refreshStudents();
            refreshDashboard();
            refreshMatches();

        } catch (NumberFormatException ex) {
            showError("Student ID must be a number.");
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void refreshDashboard() {

        if (studentCount == null || matchCount == null) {
            return;
        }

        try {
            studentCount.setText(
                    "Total Students: " + manager.getAllStudents().size());

            matchCount.setText(
                    "Potential Matches: " + manager.findMatches().size());

        } catch (SQLException ex) {
            setStatus("Could not load dashboard data.");
        }
    }

    private void setStatus(String message) {

        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this, message, "Error",
                JOptionPane.WARNING_MESSAGE);
    }

    private void showDatabaseError(SQLException ex) {

        JOptionPane.showMessageDialog(
                this,
                "Database operation failed.\n\n"
                        + ex.getMessage()
                        + "\n\nCheck MySQL and your password in "
                        + "DatabaseConnection.java.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);

        setStatus("Database operation failed.");
    }
}