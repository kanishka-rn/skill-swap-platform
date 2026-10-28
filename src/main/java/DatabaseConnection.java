import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens a MySQL connection using settings from the local environment.
 * The database password is deliberately not stored in source code.
 */
public final class DatabaseConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/skill_swap_db?serverTimezone=UTC";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = settingOrDefault("SKILLSWAP_DB_URL", DEFAULT_URL);
        String user = System.getenv("SKILLSWAP_DB_USER");
        String password = System.getenv("SKILLSWAP_DB_PASSWORD");

        if (user == null || user.isBlank() || password == null || password.isBlank()) {
            throw new SQLException(
                    "Set SKILLSWAP_DB_USER and SKILLSWAP_DB_PASSWORD before starting the application.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static String settingOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
