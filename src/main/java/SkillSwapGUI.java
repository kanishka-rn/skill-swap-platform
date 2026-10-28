import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Main window. Each tab focuses on one task to keep the student workflow clear.
 */
public class SkillSwapGUI extends JFrame {
    private static final String[] STUDENT_COLUMNS = {
            "ID", "Name", "Email", "Teaches", "Wants to learn", "Additional skills"
    };

    private final SkillSwapManager manager = new SkillSwapManager();
    private final DefaultTableModel allStudentsModel = createTableModel(STUDENT_COLUMNS);
    private final DefaultTableModel searchResultsModel = createTableModel(STUDENT_COLUMNS);
    private final DefaultTableModel matchesModel = createTableModel(new String[]{
            "Student", "Email", "Teaches", "Wants to learn",
            "Exchange partner", "Partner email"
    });
    private final JTable allStudentsTable = createTable(allStudentsModel);
    private final JTable searchResultsTable = createTable(searchResultsModel);
    private final JTable matchesTable = createTable(matchesModel);
    private final JLabel dashboardSummary = new JLabel("Loading student records...");
    private final JLabel matchesStatus =
            new JLabel("Select Find compatible exchanges to check for a match.");

    private final JTextField nameField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);
    private final JComboBox<String> teachingSkillBox = createSkillBox();
    private final JComboBox<String> learningSkillBox = createSkillBox();
    private final JTextField additionalSkillsField = new JTextField(24);
    private final JComboBox<String> searchSkillBox = createSkillBox();

    public SkillSwapGUI() {
        super("Skill Swap Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setSize(1100, 680);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", createDashboardTab());
        tabs.addTab("Register Student", createRegistrationTab());
        tabs.addTab("Search Skills", createSearchTab());
        tabs.addTab("Find Matches", createMatchesTab());
        tabs.addTab("Manage Students", createManageTab());
        add(tabs, BorderLayout.CENTER);

        refreshAllViews();
    }

    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Welcome to the Skill Swap Platform", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        dashboardSummary.setHorizontalAlignment(SwingConstants.CENTER);
        dashboardSummary.setFont(dashboardSummary.getFont().deriveFont(16f));

        JButton refreshButton = new JButton("Refresh dashboard");
        refreshButton.addActionListener(event -> refreshAllViews());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(refreshButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(dashboardSummary, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRegistrationTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        addFormRow(panel, constraints, 0, "Full name:", nameField);
        addFormRow(panel, constraints, 1, "Email address:", emailField);
        addFormRow(panel, constraints, 2, "Skill to teach:", teachingSkillBox);
        addFormRow(panel, constraints, 3, "Skill to learn:", learningSkillBox);
        addFormRow(panel, constraints, 4, "Additional skills (comma-separated):",
                additionalSkillsField);

        JLabel hint = new JLabel("Choose a skill or type a custom one in either skill box.");
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        panel.add(hint, constraints);

        JButton registerButton = new JButton("Register student");
        registerButton.addActionListener(event -> registerStudent());
        constraints.gridy = 6;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, constraints);
        return panel;
    }

    private JPanel createSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(event -> searchStudents());
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Students who teach:"));
        controls.add(searchSkillBox);
        controls.add(searchButton);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(searchResultsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMatchesTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton refreshButton = new JButton("Find compatible exchanges");
        refreshButton.addActionListener(event -> refreshMatches());
        JLabel explanation = new JLabel(
                "A match is reciprocal: each student teaches the skill the other wants.");
        JPanel descriptions = new JPanel(new BorderLayout(4, 4));
        descriptions.add(explanation, BorderLayout.NORTH);
        descriptions.add(matchesStatus, BorderLayout.SOUTH);
        JPanel controls = new JPanel(new BorderLayout(8, 8));
        controls.add(descriptions, BorderLayout.CENTER);
        controls.add(refreshButton, BorderLayout.EAST);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(matchesTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createManageTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton refreshButton = new JButton("Refresh records");
        refreshButton.addActionListener(event -> refreshAllViews());
        JButton deleteButton = new JButton("Delete selected student");
        deleteButton.addActionListener(event -> deleteSelectedStudent());
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.add(refreshButton);
        controls.add(deleteButton);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(allStudentsTable), BorderLayout.CENTER);
        return panel;
    }

    private void registerStudent() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            SkillStudent student = new SkillStudent(
                    0,
                    name,
                    email,
                    selectedSkill(teachingSkillBox, "Skill to teach"),
                    selectedSkill(learningSkillBox, "Skill to learn"),
                    additionalSkillsField.getText());
            runDatabaseTask(() -> {
                manager.addStudent(student);
                return null;
            }, ignored -> {
                clearRegistrationForm();
                refreshAllViews();
                showMessage("Student registered successfully.\n\n" + student.getDisplayInfo(),
                        "Registration complete",
                        JOptionPane.INFORMATION_MESSAGE);
            }, exception -> {
                if (SkillSwapManager.isDuplicateEmail(exception)) {
                    showMessage("That email address is already registered. Use a different email.",
                            "Duplicate email", JOptionPane.WARNING_MESSAGE);
                } else {
                    showDatabaseError("Student registration", exception);
                }
            });
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage(), "Check the form", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void searchStudents() {
        try {
            String skill = selectedSkill(searchSkillBox, "Search skill");
            runDatabaseTask(() -> manager.searchByTeachingSkill(skill), students -> {
                fillStudentTable(searchResultsModel, students);
                if (students.isEmpty()) {
                    showMessage("No students were found teaching \"" + skill + "\".",
                            "No results", JOptionPane.INFORMATION_MESSAGE);
                }
            }, exception -> showDatabaseError("Teaching-skill search", exception));
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage(), "Enter a search skill",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshAllViews() {
        runDatabaseTask(manager::getAllStudents, students -> {
            fillStudentTable(allStudentsModel, students);
            dashboardSummary.setText(
                    "<html><div style='text-align: center;'>"
                            + "<b>" + students.size() + "</b> registered student(s)<br><br>"
                            + "Use the tabs above to register students, search teaching skills, "
                            + "find reciprocal exchanges, or manage records."
                            + "</div></html>");
            refreshMatches();
        }, exception -> {
            dashboardSummary.setText("Could not load student records. Check the database settings.");
            showDatabaseError("Loading student records", exception);
        });
    }

    private void refreshMatches() {
        matchesModel.setRowCount(0);
        matchesStatus.setText("Checking the latest student records...");
        runDatabaseTask(manager::getAllStudents, students -> {
            List<SkillSwapManager.SkillMatch> matches =
                    SkillSwapManager.findCompatibleMatches(students);
            matchesModel.setRowCount(0);
            for (SkillSwapManager.SkillMatch match : matches) {
                SkillStudent first = match.first();
                SkillStudent second = match.second();
                matchesModel.addRow(new Object[]{
                        first.getName(), first.getEmail(), first.getTeachingSkill(),
                        first.getLearningSkill(), second.getName(), second.getEmail()
                });
            }
            if (students.isEmpty()) {
                matchesStatus.setText(
                        "No student records found. Register students before looking for exchanges.");
            } else if (matches.isEmpty()) {
                matchesStatus.setText(
                        "No compatible exchanges found. Register students with complementary skills and try again.");
            } else {
                matchesStatus.setText("Found " + matches.size() + " compatible exchange(s).");
            }
        }, exception -> {
            matchesStatus.setText("Could not load compatible exchanges.");
            showDatabaseError("Finding reciprocal exchanges", exception);
        });
    }

    private void deleteSelectedStudent() {
        int selectedRow = allStudentsTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Select a student record before deleting.",
                    "No student selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = allStudentsTable.convertRowIndexToModel(selectedRow);
        long id = ((Number) allStudentsModel.getValueAt(modelRow, 0)).longValue();
        String studentName = String.valueOf(allStudentsModel.getValueAt(modelRow, 1));
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Permanently delete the record for " + studentName + "?",
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        runDatabaseTask(() -> manager.deleteStudent(id), deleted -> {
            if (deleted) {
                refreshAllViews();
                showMessage("Student record deleted.", "Deletion complete",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage("The selected record no longer exists. Refresh and try again.",
                        "Record not found", JOptionPane.INFORMATION_MESSAGE);
                refreshAllViews();
            }
        }, exception -> showDatabaseError("Deleting student record", exception));
    }

    private <T> void runDatabaseTask(
            DatabaseTask<T> task,
            Consumer<T> onSuccess,
            Consumer<SQLException> onDatabaseError) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws SQLException {
                return task.run();
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showMessage("The database operation was interrupted. Please try again.",
                            "Operation interrupted", JOptionPane.ERROR_MESSAGE);
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    if (cause instanceof SQLException sqlException) {
                        onDatabaseError.accept(sqlException);
                    } else {
                        System.err.println("Unexpected error while running a database task:");
                        if (cause != null) {
                            cause.printStackTrace(System.err);
                        }
                        String detail = cause == null || cause.getMessage() == null
                                ? "No additional details are available."
                                : cause.getMessage();
                        showMessage("The operation failed unexpectedly.\n\n" + detail,
                                "Operation error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    @FunctionalInterface
    private interface DatabaseTask<T> {
        T run() throws SQLException;
    }

    private static void fillStudentTable(
            DefaultTableModel model, List<SkillStudent> students) {
        model.setRowCount(0);
        for (SkillStudent student : students) {
            model.addRow(new Object[]{
                    student.getId(), student.getName(), student.getEmail(),
                    student.getTeachingSkill(), student.getLearningSkill(),
                    student.getAdditionalSkills()
            });
        }
    }

    private void clearRegistrationForm() {
        nameField.setText("");
        emailField.setText("");
        teachingSkillBox.setSelectedItem("");
        learningSkillBox.setSelectedItem("");
        additionalSkillsField.setText("");
        nameField.requestFocusInWindow();
    }

    private void showDatabaseError(String operation, SQLException exception) {
        System.err.println(operation + " failed. SQLState=" + exception.getSQLState()
                + ", errorCode=" + exception.getErrorCode());
        exception.printStackTrace(System.err);

        String message = databaseErrorMessage(exception);
        String detail = exception.getMessage() == null
                ? "No additional database error details were provided."
                : exception.getMessage();
        showMessage(
                operation + " failed.\n\n" + message + "\n\n"
                        + detail
                        + "\n\nCheck that MySQL Server is running, the schema matches the application, "
                        + "and the SKILLSWAP_DB_* variables are set.",
                "Database error",
                JOptionPane.ERROR_MESSAGE);
    }

    private static String databaseErrorMessage(SQLException exception) {
        String sqlState = exception.getSQLState();
        int errorCode = exception.getErrorCode();

        if (errorCode == 1045 || "28000".equals(sqlState)) {
            return "MySQL rejected the login. Check SKILLSWAP_DB_USER and "
                    + "SKILLSWAP_DB_PASSWORD; the password is not displayed here.";
        }
        if (errorCode == 1049) {
            return "The skill_swap_db database was not found. Confirm the database name in MySQL.";
        }
        if (errorCode == 1054 || errorCode == 1146
                || "42S02".equals(sqlState) || "42S22".equals(sqlState)) {
            return "A required table or column is missing. Compare students with database/schema.sql.";
        }
        if (sqlState != null && sqlState.startsWith("08")) {
            return "MySQL could not be reached. Check that the MySQL80 service is running "
                    + "and listening on localhost:3306.";
        }
        return "Check the MySQL service, database schema, and application database account.";
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private static String selectedSkill(JComboBox<String> skillBox, String label) {
        Object value = skillBox.isEditable()
                ? skillBox.getEditor().getItem()
                : skillBox.getSelectedItem();
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException(label + " cannot be empty.");
        }
        return value.toString().trim();
    }

    private static JComboBox<String> createSkillBox() {
        JComboBox<String> skillBox =
                new JComboBox<>(SkillStudent.getPredefinedSkills().toArray(String[]::new));
        skillBox.setEditable(true);
        skillBox.setSelectedItem("");
        skillBox.setPreferredSize(new Dimension(260, 28));
        return skillBox;
    }

    private static void addFormRow(
            JPanel panel, GridBagConstraints constraints, int row,
            String labelText, java.awt.Component field) {
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.gridx = 0;
        panel.add(new JLabel(labelText), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(field, constraints);
    }

    private static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        return table;
    }

    private static DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
