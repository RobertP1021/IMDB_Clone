package org.example;

import java.util.ArrayList;
import java.util.List;

public class Actor implements Comparable<Object> {

    private String name;
    private List<NameTypePair> roles;
    private String biography;

    public Actor(String name, List<NameTypePair> roles, String biography) {
        this.name = name;
        this.roles = roles;
        this.biography = biography;
    }

    public Actor(){
        this.name = null;
        this.roles = new ArrayList<>();
        this.biography = null;
    }

    public String getName() {
        return name;
    }

    public List<NameTypePair> getRoles() {
        return roles;
    }

    public String getBiography() {
        return biography;
    }

    public static class NameTypePair {
        private String name;
        private String type;

        public NameTypePair(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setRoles(List<NameTypePair> roles) {
        this.roles = roles;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        String s = "Actor{" +
                "name='" + name + '\'' +
                ", roles=";
        for (NameTypePair pair: roles){
            s += pair.name+ "-"+pair.type + ", ";
        }
        s += ", biography='" + biography + '\'' +
                '}';
        return s;
    }

    public int compareTo(Object o) {
        if (o instanceof Actor) {
            Actor other = (Actor) o;
            return this.getName().compareTo(other.getName());
        }
        return 1;
    }

}
