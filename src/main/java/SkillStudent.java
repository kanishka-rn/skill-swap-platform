import java.util.List;

/**
 * A student who has added teaching, learning, and optional extra skills.
 */
public class SkillStudent extends Student {
    private static final List<String> PREDEFINED_SKILLS = List.of(
            "Java", "Python", "C++", "HTML", "CSS", "JavaScript",
            "SQL", "Photoshop", "Web Design", "Data Science");

    private String teachingSkill;
    private String learningSkill;
    private String additionalSkills;

    public SkillStudent(long id, String name, String email, String teachingSkill,
                        String learningSkill, String additionalSkills) {
        super(id, name, email);
        setTeachingSkill(teachingSkill);
        setLearningSkill(learningSkill);
        setAdditionalSkills(additionalSkills);
    }

    public static List<String> getPredefinedSkills() {
        return PREDEFINED_SKILLS;
    }

    public String getTeachingSkill() {
        return teachingSkill;
    }

    public void setTeachingSkill(String teachingSkill) {
        this.teachingSkill = requireText(teachingSkill, "Skill to teach");
    }

    public String getLearningSkill() {
        return learningSkill;
    }

    public void setLearningSkill(String learningSkill) {
        this.learningSkill = requireText(learningSkill, "Skill to learn");
    }

    public String getAdditionalSkills() {
        return additionalSkills;
    }

    public void setAdditionalSkills(String additionalSkills) {
        this.additionalSkills = additionalSkills == null ? "" : additionalSkills.trim();
    }

    /**
     * Overrides Student's description so polymorphic calls show the skills too.
     */
    @Override
    public String getDisplayInfo() {
        return super.getDisplayInfo() + " | Teaches: " + teachingSkill
                + " | Wants to learn: " + learningSkill;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
