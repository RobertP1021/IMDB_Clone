package org.example;
import java.util.*;
import java.time.LocalDateTime;

public abstract class User implements Comparable<User>,Observer {

    public static class InformationBuilder {
        private Credentials credentials;
        private String name;
        private String country;
        private int age;
        private char gender;
        private LocalDateTime birthDate;

        public InformationBuilder credentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public InformationBuilder name(String name){
            this.name = name;
            return this;
        }

        public InformationBuilder country(String country) {
            this.country = country;
            return this;
        }

        public InformationBuilder age(int age) {
            this.age = age;
            return this;
        }

        public InformationBuilder gender(char gender) {
            this.gender = gender;
            return this;
        }

        public InformationBuilder birthDate(LocalDateTime birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Information build() {
            return new Information(credentials, name, country, age, gender, birthDate);
        }
    }

    public static class Information {
        private Credentials credentials;
        private String name;
        private String country;
        private int age;
        private char gender;
        private LocalDateTime birthDate;

        @Override
        public String toString() {
            return "Information{" +
                    "credentials=" + credentials +
                    "\nname='" + name + '\'' +
                    "\ncountry='" + country + '\'' +
                    "\nage=" + age +
                    "\ngender=" + gender +
                    "\nbirthDate=" + birthDate +
                    '}';
        }

        public Information(Credentials credentials, String name, String country, int age, char gender, LocalDateTime birthDate) {
            this.credentials = credentials;
            this.name = name;
            this.country = country;
            this.age = age;
            this.gender = gender;
            this.birthDate = birthDate;
        }

        public Credentials getCredentials() {
            return credentials;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public int getAge() {
            return age;
        }

        public void setCredentials(Credentials credentials) {
            this.credentials = credentials;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setGender(char gender) {
            this.gender = gender;
        }

        public void setBirthDate(LocalDateTime birthDate) {
            this.birthDate = birthDate;
        }

        public char getGender() {
            return gender;
        }

        public LocalDateTime getBirthDate() {
            return birthDate;
        }
    }
    public Information userInfo;
    public AccountType accountType;
    public String username;
    public int experience;
    public List<String> notifications;
    public ArrayList<String> userNotifications;

    public void setExperience(int experience) {
        this.experience = experience;
    }


    public SortedSet<Comparable<?>> favorites;

    public User(Information userInfo, AccountType accountType) {
        this.userInfo = userInfo;
        this.accountType = accountType;
        this.username = generateUniqueUsername(userInfo.getName());
        this.experience = 0;
        this.notifications = new ArrayList<>();
        this.userNotifications = new ArrayList<>();
        this.favorites = new TreeSet<>();
    }

    @Override
    public void sendNotification(ArrayList<String> notifications, String notification){
        userNotifications.add(notification);
    }

    private String generateUniqueUsername(String name) {
        return name.toLowerCase().replace(" ", "_") + "_" + new Random().nextInt(1000);
    }

    public abstract void addFavorite(Comparable<?> item);

    public abstract void removeFavorite(Comparable<?> item);
    public void addFavoriteProduction(Production production){
        favorites.add(production);
    }

    public void removeFavoriteProduction(Production production){
        favorites.remove(production);
    }

    public abstract void updateExperience();

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public void setFavorites(SortedSet<Comparable<?>> favorites) {
        this.favorites = favorites;
    }

    public abstract void logout();

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Information getInformation(){
        return userInfo;
    }

    public String getUsername(){
        return username;
    }

    @Override
    public String toString() {
        return "User{" +
                "userInfo=" + userInfo +
                "accountType=" + accountType +
                "username='" + username + '\'' +
                "experience=" + experience +
                "notifications=" + notifications +
                "favorites=" + favorites +
                '}';
    }

    @Override
    public int compareTo(User otherUser) {
        return Integer.compare(otherUser.experience, this.experience);
    }
}

