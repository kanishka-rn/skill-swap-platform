public class Student extends Person {

    public Student(String name, String email) {
        super(name, email);
    }

    @Override
    public String getRoleDescription() {
        return "Student";
    }
}