import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Starts the desktop application on Swing's event-dispatch thread. */
public class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception exception) {
                System.err.println("Using the default Swing appearance: "
                        + exception.getMessage());
            }
            new SkillSwapGUI().setVisible(true);
        });
    }
}
