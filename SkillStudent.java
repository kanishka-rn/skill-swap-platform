public class SkillStudent extends Student {

    private int id;
    private String teachSkill;
    private String learnSkill;
    private String additionalSkills;

    public SkillStudent(int id, String name, String email,
                        String teachSkill, String learnSkill,
                        String additionalSkills) {

        super(name, email);

        this.id = id;
        this.teachSkill = teachSkill;
        this.learnSkill = learnSkill;
        this.additionalSkills = additionalSkills;
    }

    public int getId() {
        return id;
    }

    public String getTeachSkill() {
        return teachSkill;
    }

    public String getLearnSkill() {
        return learnSkill;
    }

    public String getAdditionalSkills() {
        return additionalSkills;
    }

    @Override
    public String getRoleDescription() {
        return "Skill Swap Student";
    }
}