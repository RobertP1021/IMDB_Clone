package org.example;

public class Credentials {

    private final String email;
    private final String password;

    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "\nemail='" + email + '\'' +
                "\npassword='" + password + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public static Credentials createCredentials(String email, String password) {
        return new Credentials(email, password);
    }
}
