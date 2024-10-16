package org.example;
public class UserFactory {

    public static <T extends Comparable<T>> User createUser(User.Information userInfo, AccountType accountType) {
        switch (accountType) {
            case REGULAR:
                return new Regular(userInfo);
            case CONTRIBUTOR:
                return new Contributor(userInfo);
            case ADMIN:
                return new Admin(userInfo);
            default:
                throw new IllegalArgumentException("Invalid account type");
        }
    }
}
