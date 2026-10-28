import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the database operations used by the Swing interface.
 * SQL values are always supplied through PreparedStatement parameters.
 */
public class SkillSwapManager {
    private static final String SELECT_COLUMNS =
            "id, name, email, teach_skill, learn_skill, additional_skills";

    private static final String INSERT_SQL = """
            INSERT INTO students (name, email, teach_skill, learn_skill, additional_skills)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String LIST_SQL =
            "SELECT " + SELECT_COLUMNS + " FROM students ORDER BY name, id";
    private static final String SEARCH_SQL = """
            SELECT id, name, email, teach_skill, learn_skill, additional_skills
            FROM students
            WHERE LOWER(teach_skill) LIKE LOWER(?)
            ORDER BY name, id
            """;
    private static final String DELETE_SQL = "DELETE FROM students WHERE id = ?";

    /** A reciprocal pair: each student teaches what the other wants to learn. */
    public record SkillMatch(SkillStudent first, SkillStudent second) {
    }

    public List<SkillStudent> getAllStudents() throws SQLException {
        List<SkillStudent> students = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(LIST_SQL);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                students.add(readStudent(results));
            }
        }
        return students;
    }

    public void addStudent(SkillStudent student) throws SQLException {
        if (student == null) {
            throw new IllegalArgumentException("Student details are required.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getTeachingSkill());
            statement.setString(4, student.getLearningSkill());
            statement.setString(5, student.getAdditionalSkills());
            statement.executeUpdate();
        }
    }

    public List<SkillStudent> searchByTeachingSkill(String skill) throws SQLException {
        if (skill == null || skill.isBlank()) {
            throw new IllegalArgumentException("Enter a skill to search for.");
        }

        List<SkillStudent> students = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEARCH_SQL)) {
            statement.setString(1, "%" + skill.trim() + "%");
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    students.add(readStudent(results));
                }
            }
        }
        return students;
    }

    /**
     * Returns each reciprocal exchange once; a student cannot match with themself.
     */
    public List<SkillMatch> findCompatibleMatches() throws SQLException {
        return findCompatibleMatches(getAllStudents());
    }

    static List<SkillMatch> findCompatibleMatches(List<SkillStudent> students) {
        List<SkillMatch> matches = new ArrayList<>();

        for (int firstIndex = 0; firstIndex < students.size(); firstIndex++) {
            SkillStudent first = students.get(firstIndex);
            if (first == null) {
                continue;
            }
            for (int secondIndex = firstIndex + 1; secondIndex < students.size(); secondIndex++) {
                SkillStudent second = students.get(secondIndex);
                if (second == null || isSameStudent(first, second)) {
                    continue;
                }
                boolean firstCanHelpSecond = sameSkill(
                        first.getTeachingSkill(), second.getLearningSkill());
                boolean secondCanHelpFirst = sameSkill(
                        second.getTeachingSkill(), first.getLearningSkill());

                if (firstCanHelpSecond && secondCanHelpFirst) {
                    matches.add(new SkillMatch(first, second));
                }
            }
        }
        return matches;
    }

    public boolean deleteStudent(long id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Select a valid student record.");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    /** MySQL reports duplicate unique-key values with vendor error code 1062. */
    public static boolean isDuplicateEmail(SQLException exception) {
        for (SQLException current = exception; current != null; current = current.getNextException()) {
            if (current.getErrorCode() == 1062) {
                return true;
            }
        }
        return false;
    }

    private static SkillStudent readStudent(ResultSet results) throws SQLException {
        return new SkillStudent(
                results.getLong("id"),
                results.getString("name"),
                results.getString("email"),
                results.getString("teach_skill"),
                results.getString("learn_skill"),
                results.getString("additional_skills"));
    }

    private static boolean isSameStudent(SkillStudent first, SkillStudent second) {
        return first == second
                || (first.getId() > 0 && first.getId() == second.getId());
    }

    static boolean sameSkill(String first, String second) {
        return first != null && second != null
                && !first.isBlank() && !second.isBlank()
                && first.trim().equalsIgnoreCase(second.trim());
    }
}
