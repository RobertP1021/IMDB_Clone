package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.text.DecimalFormat;

public abstract class Production implements Comparable<Object> {
    protected String title;
    protected List<String> directors;
    protected List<String> actors;
    protected List<Genre> genres;
    protected List<Rating> ratings;
    protected String plotDescription;
    protected Double averageRating;


    public Production(String title, List<String> directors, List<String> actors, List<Genre> genres,
                      List<Rating> ratings, String plotDescription) {
        this.title = title;
        this.directors = directors;
        this.actors = actors;
        this.genres = genres;
        this.ratings = ratings;
        this.plotDescription = plotDescription;
        this.averageRating = calculateAverageRating();

    }
    public Production(){
        this.title = null;
        this.directors = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.plotDescription = null;
        this.averageRating = null;
    }

    public abstract void displayInfo();

    public String getTitle() {
        return title;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public List<String> getActors() {
        return actors;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public String getPlotDescription() {
        return plotDescription;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Double calculateAverageRating() {
        if (ratings != null && !ratings.isEmpty()) {
            double sum = 0.0;
            for (Rating rating : ratings) {
                sum += rating.getScore();
            }

            double average = sum / ratings.size();

            // Round to one decimal place
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            return Double.parseDouble(decimalFormat.format(average));
        } else {
            return 0.0;
        }
    }
    public void addRating(String username, int score, String comment) {
        Rating rating = new Rating(username, score, comment);
        ratings.add(rating);
//        List<Regular> regulars = IMDB.getInstance().getRegularUsers();
//        for(Regular regular : regulars){
//            if(regular.getUsername().equals(username)){
//                rating.registerObserver(regular);
//            }
//        }
        averageRating = calculateAverageRating();
    }

    public void removeRating(String username) {
        Iterator<Rating> iterator = ratings.iterator();
        while (iterator.hasNext()) {
            Rating rating = iterator.next();
            if (rating.getUsername().equalsIgnoreCase(username)) {
                iterator.remove();
                System.out.println("Recenzia a fost stearsa.");
                ratings.remove(rating);
                break;
            }
        }
        averageRating = calculateAverageRating();
    }


    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }

        if (o instanceof Production) {
            return this.title.compareTo(((Production) o).getTitle());
        } else {
            return 1;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
        this.averageRating = calculateAverageRating();
    }

    public void setPlotDescription(String plotDescription) {
        this.plotDescription = plotDescription;
    }
}


