package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Request implements Subject {

    private ArrayList<Observer> observers = new ArrayList<>();
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


    public enum RequestType {
        MOVIE_ISSUE,
        ACTOR_ISSUE,
        DELETE_ACCOUNT,
        OTHERS
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RequestType requestType;
    private LocalDateTime creationDate;
    private String titleOrActorName;
    private String problemDescription;
    private String createdByUsername;
    private String assignedToUsername;

    public Request(RequestType requestType,String titleOrActorName, String problemDescription,
                   String createdByUsername, String assignedToUsername) {
        this.requestType = requestType;
        this.creationDate = LocalDateTime.now();
        this.titleOrActorName = titleOrActorName;
        this.problemDescription = problemDescription;
        this.createdByUsername = createdByUsername;
        this.assignedToUsername = assignedToUsername;
    }

    public Request(){
        this.requestType = null;
        this.creationDate = LocalDateTime.now();
        this.titleOrActorName = null;
        this.problemDescription = null;
        this.createdByUsername = null;
        this.assignedToUsername = null;
    }

//    private String determineAssignedToUsername() {
//        switch (requestType) {
//            case DELETE_ACCOUNT:
//            case OTHERS:
//                return "ADMIN";
//            case MOVIE_ISSUE:
//            case ACTOR_ISSUE:
//                return assignedToUsername;
//            default:
//                return null;
//        }
//    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setTitleOrActorName(String titleOrActorName) {
        this.titleOrActorName = titleOrActorName;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public void setAssignedToUsername(String assignedToUsername) {
        this.assignedToUsername = assignedToUsername;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public String getCreationDateFormatted() {
        return creationDate.format(DATE_TIME_FORMATTER);
    }

    public String getTitleOrActorName() {
        return titleOrActorName;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public String getAssignedToUsername() {
        return assignedToUsername;
    }

    @Override
    public String toString() {
        if(titleOrActorName != null){
            return "Request{" +
                    "requestType=" + requestType +
                    ", creationDate=" + getCreationDateFormatted() +
                    ", titleOrActorName='" + titleOrActorName + '\'' +
                    ", problemDescription='" + problemDescription + '\'' +
                    ", createdByUsername='" + createdByUsername + '\'' +
                    ", assignedToUsername='" + assignedToUsername + '\'' +
                    '}';
        }else{
            return "Request{" +
                    "requestType=" + requestType +
                    ", creationDate=" + getCreationDateFormatted() +
                    ", problemDescription='" + problemDescription + '\'' +
                    ", createdByUsername='" + createdByUsername + '\'' +
                    ", assignedToUsername='" + assignedToUsername + '\'' +
                    '}';
        }

    }
}
