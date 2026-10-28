import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SkillSwapManagerTest {
    @Test
    void findsCaseInsensitiveReciprocalPairsExactlyOnce() {
        SkillStudent javaLearner = student(
                1, "Ava", "ava@example.com", "Java", "Python");
        SkillStudent pythonLearner = student(
                2, "Ben", "ben@example.com", "python", "JAVA");
        SkillStudent unrelated = student(
                3, "Cam", "cam@example.com", "SQL", "CSS");

        List<SkillSwapManager.SkillMatch> matches =
                SkillSwapManager.findCompatibleMatches(
                        List.of(javaLearner, pythonLearner, unrelated));

        assertEquals(1, matches.size());
        assertEquals(javaLearner, matches.get(0).first());
        assertEquals(pythonLearner, matches.get(0).second());
    }

    @Test
    void findsTheFiveDesignedSampleExchangePairs() {
        List<SkillStudent> sampleStudents = List.of(
                student(1, "Aanya Rao", "skillswap.demo01@example.com", "Java", "Python"),
                student(2, "Vihaan Iyer", "skillswap.demo02@example.com", "Python", "Java"),
                student(3, "Meera Nair", "skillswap.demo03@example.com",
                        "Web Development", "UI/UX Design"),
                student(4, "Arjun Mehta", "skillswap.demo04@example.com",
                        "UI/UX Design", "Web Development"),
                student(5, "Ishita Kapoor", "skillswap.demo05@example.com", "SQL", "Excel"),
                student(6, "Kabir Shah", "skillswap.demo06@example.com", "Excel", "SQL"),
                student(7, "Saanvi Reddy", "skillswap.demo07@example.com", "HTML", "CSS"),
                student(8, "Aditya Menon", "skillswap.demo08@example.com", "CSS", "HTML"),
                student(9, "Diya Kulkarni", "skillswap.demo09@example.com",
                        "Data Science", "Machine Learning"),
                student(10, "Rohan Das", "skillswap.demo10@example.com",
                        "Machine Learning", "Data Science"));

        Set<Set<String>> expectedPairs = Set.of(
                Set.of("skillswap.demo01@example.com", "skillswap.demo02@example.com"),
                Set.of("skillswap.demo03@example.com", "skillswap.demo04@example.com"),
                Set.of("skillswap.demo05@example.com", "skillswap.demo06@example.com"),
                Set.of("skillswap.demo07@example.com", "skillswap.demo08@example.com"),
                Set.of("skillswap.demo09@example.com", "skillswap.demo10@example.com"));

        Set<Set<String>> actualPairs = new HashSet<>();
        for (SkillSwapManager.SkillMatch match :
                SkillSwapManager.findCompatibleMatches(sampleStudents)) {
            actualPairs.add(Set.of(match.first().getEmail(), match.second().getEmail()));
        }

        assertEquals(expectedPairs, actualPairs);
    }

    @Test
    void doesNotMatchAStudentWithThemselfOrCreatePairsForSingleStudent() {
        SkillStudent student = student(
                1, "Alex", "alex@example.com", "Java", "Java");

        assertTrue(SkillSwapManager.findCompatibleMatches(List.of(student)).isEmpty());
    }

    @Test
    void returnsNoMatchesForAnEmptyStudentList() {
        assertTrue(SkillSwapManager.findCompatibleMatches(List.of()).isEmpty());
    }

    @Test
    void ignoresNullEntriesAndDoesNotMatchTheSamePersistedStudentTwice() {
        SkillStudent student = student(12, "Alex", "alex@example.com", "Java", "Python");

        assertTrue(SkillSwapManager.findCompatibleMatches(
                Arrays.asList(student, null, student)).isEmpty());
    }

    @Test
    void blankOrNullSkillsAreNotConsideredEqual() {
        assertFalse(SkillSwapManager.sameSkill(null, "Java"));
        assertFalse(SkillSwapManager.sameSkill("Java", null));
        assertFalse(SkillSwapManager.sameSkill(" ", "Java"));
        assertFalse(SkillSwapManager.sameSkill("Java", "\t"));
        assertTrue(SkillSwapManager.sameSkill(" Java ", "java\t"));
    }

    private static SkillStudent student(
            long id, String name, String email, String teach, String learn) {
        return new SkillStudent(id, name, email, teach, learn, "");
    }
}
