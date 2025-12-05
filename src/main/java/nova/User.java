package nova;

// user ist nur in der Adresse sichtbar, da wir es nicht für notwendig empfunden haben
public class User {
    private final String username;

    public User(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}
