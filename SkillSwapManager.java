import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillSwapManager {

    // Register a student in MySQL.
    public void registerStudent(
            String name,
            String email,
            String teachSkill,
            String learnSkill,
            String additionalSkills) throws SQLException {

        String sql = "INSERT INTO students "
                + "(name, email, teach_skill, learn_skill, additional_skills) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, teachSkill);
            ps.setString(4, learnSkill);
            ps.setString(5, additionalSkills);

            ps.executeUpdate();
        }
    }

    // Return all registered students.
    public List<SkillStudent> getAllStudents()
            throws SQLException {

        List<SkillStudent> students = new ArrayList<>();

        String sql = "SELECT * FROM students ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                students.add(readStudent(rs));
            }
        }

        return students;
    }

    // Search students by the skill they teach.
    public List<SkillStudent> searchBySkill(String skill)
            throws SQLException {

        List<SkillStudent> students = new ArrayList<>();

        String sql = "SELECT * FROM students "
                + "WHERE LOWER(teach_skill) = LOWER(?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, skill);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(readStudent(rs));
                }
            }
        }

        return students;
    }

    // Find students who can teach each other.
    public List<SkillStudent[]> findMatches()
            throws SQLException {

        List<SkillStudent> students = getAllStudents();
        List<SkillStudent[]> matches = new ArrayList<>();

        for (int i = 0; i < students.size(); i++) {

            for (int j = i + 1; j < students.size(); j++) {

                SkillStudent first = students.get(i);
                SkillStudent second = students.get(j);

                boolean firstCanTeachSecond =
                        first.getTeachSkill().equalsIgnoreCase(
                                second.getLearnSkill());

                boolean secondCanTeachFirst =
                        second.getTeachSkill().equalsIgnoreCase(
                                first.getLearnSkill());

                if (firstCanTeachSecond && secondCanTeachFirst) {
                    matches.add(new SkillStudent[]{first, second});
                }
            }
        }

        return matches;
    }

    // Delete a student using their ID.
    public void deleteStudent(int id)
            throws SQLException {

        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new SQLException(
                        "No student found with ID " + id);
            }
        }
    }

    // Convert a database record into a Java object.
    private SkillStudent readStudent(ResultSet rs)
            throws SQLException {

        return new SkillStudent(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("teach_skill"),
                rs.getString("learn_skill"),
                rs.getString("additional_skills")
        );
    }
}