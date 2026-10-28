/**
 * Common information shared by people who use the platform.
 *
 * This abstract class demonstrates abstraction: every subclass must decide
 * how its information should be presented to the user.
 */
public abstract class Person {
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final long id;
    private String name;
    private String email;

    protected Person(long id, String name, String email) {
        this.id = id;
        setName(name);
        setEmail(email);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireText(name, "Name");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String normalizedEmail = requireText(email, "Email");
        if (!normalizedEmail.matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        this.email = normalizedEmail;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    /** Returns a short description; subclasses provide their own version. */
    public abstract String getDisplayInfo();
}
