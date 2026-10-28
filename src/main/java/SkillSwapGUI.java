
import javax.swing.*;
import java.awt.*;

public class SkillSwapGUI extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> teachSkill;
    private JComboBox<String> learnSkill;

    public SkillSwapGUI() {
        setTitle("Skill Swap Platform");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("SKILL SWAP PLATFORM", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(new Color(29, 78, 137));

        mainPanel.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 15));

        nameField = new JTextField();
        emailField = new JTextField();

        String[] skills = {
                "Java", "Python", "C++", "JavaScript",
                "HTML", "CSS", "Graphic Design", "Photoshop",
                "Web Design", "SQL", "Data Science", "Other"
        };

        teachSkill = new JComboBox<>(skills);
        teachSkill.setEditable(true);

        learnSkill = new JComboBox<>(skills);
        learnSkill.setEditable(true);

        form.add(new JLabel("Student Name:"));
        form.add(nameField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Skill to Teach:"));
        form.add(teachSkill);

        form.add(new JLabel("Skill to Learn:"));
        form.add(learnSkill);

        JButton registerButton = new JButton("Register Student");
        JButton clearButton = new JButton("Clear");

        form.add(registerButton);
        form.add(clearButton);

        registerButton.addActionListener(e -> registerStudent());
        clearButton.addActionListener(e -> clearForm());

        mainPanel.add(form, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "Learn together. Grow together.",
                JLabel.CENTER
        );

        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void registerStudent() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        String teach = teachSkill.getSelectedItem().toString();
        String learn = learnSkill.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter your name and email."
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Registration form completed!\n\n"
                        + "Name: " + name
                        + "\nEmail: " + email
                        + "\nTeaching: " + teach
                        + "\nLearning: " + learn
                        + "\n\nDatabase connection will be integrated next."
        );
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        teachSkill.setSelectedIndex(0);
        learnSkill.setSelectedIndex(1);
    }
}
