import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SkillStudentTest {
    @Test
    void trimsValidStudentFields() {
        SkillStudent student = new SkillStudent(
                0, "  Alex Student  ", "  alex@example.com  ",
                " Java ", " Python ", " Git, SQL ");

        assertEquals("Alex Student", student.getName());
        assertEquals("alex@example.com", student.getEmail());
        assertEquals("Java", student.getTeachingSkill());
        assertEquals("Python", student.getLearningSkill());
        assertEquals("Git, SQL", student.getAdditionalSkills());
    }

    @Test
    void rejectsBlankRequiredFields() {
        assertThrows(IllegalArgumentException.class,
                () -> new SkillStudent(0, " ", "alex@example.com", "Java", "Python", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new SkillStudent(0, "Alex", "alex@example.com", "", "Python", ""));
    }

    @Test
    void rejectsMalformedEmailAddresses() {
        assertThrows(IllegalArgumentException.class,
                () -> new SkillStudent(0, "Alex", "not-an-email", "Java", "Python", ""));
    }
}
