package dal;

public class Config {
    public static String getUrl() {
        return "jdbc:mysql://localhost:3306/my_tunes";
    }

    public static String getUser() {
        return "admin";
    }

    public static String getPassword() {
        return "admin";
    }
}
