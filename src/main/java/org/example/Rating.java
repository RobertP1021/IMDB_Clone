package org.example;

import java.util.ArrayList;

public class Rating implements Subject {

    private ArrayList<Observer> observers = new ArrayList<>();
    private String username;
    private int score;
    private String comments;

    public Rating(String username, int score, String comments) {
        this.username = username;
        this.score = validateAndSetScore(score);
        this.comments = comments;
    }
    public Rating(){
        this.username = null;
        this.score = 0;
        this.comments = null;
    }

    private int validateAndSetScore(int score) {
        if (score < 1) {
            return 1;
        } else if (score > 10) {
            return 10;
        } else {
            return score;
        }
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public String getComments() {
        return comments;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "username='" + username + '\'' +
                ", score=" + score +
                ", comments='" + comments + '\'' +
                '}';
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add((User)observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove((User)observer);
    }

    public ArrayList<Observer> getObservers() {
        return observers;
    }

    @Override
    public void notifyObservers(String message, int notificationType) {
        if(notificationType == 1){
            for(Observer observer : observers){
                observer.sendNotification(((User) observer).userNotifications, message);
                break;
            }
        }else if(notificationType == 2){
            int i = 0;
            for(Observer observer : observers) {
                if(i == 0){
                    i++;
                    continue;
                }
                observer.sendNotification(((User)observer).userNotifications, message);
            }
        }
    }

}
