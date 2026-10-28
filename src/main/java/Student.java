/**
 * A student is a person and supplies the general student description.
 */
public class Student extends Person {
    public Student(long id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getDisplayInfo() {
        return getName() + " (" + getEmail() + ")";
    }
}
