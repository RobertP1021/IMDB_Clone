package org.example;
import java.util.ArrayList;
import java.util.List;

public class Movie extends Production{
    private String duration;
    private int releaseYear;

    public Movie(String title, List<String> directors, List<String> actors, List<Genre> genres,
                 List<Rating> ratings, String plotDescription, String duration, int releaseYear) {
        super(title, directors, actors, genres, ratings, plotDescription);
        this.duration = duration;
        this.releaseYear = releaseYear;
    }

    public Movie(){
        this.title = null;
        this.directors = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.plotDescription = null;
        this.duration = null;
        this.releaseYear = 0;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "duration='" + duration + '\'' +
                ", releaseYear=" + releaseYear +
                ", title='" + title + '\'' +
                ", directors=" + directors +
                ", actors=" + actors +
                ", genres=" + genres +
                ", ratings=" + ratings +
                ", plotDescription='" + plotDescription + '\'' +
                ", averageRating=" + averageRating +
                '}';
    }

    @Override
    public void displayInfo() {
        System.out.println("Movie Title: " + title);
        System.out.println("Directors: " + directors);
        System.out.println("Actors: " + actors);
        System.out.println("Genres: " + genres);
        System.out.println("Ratings:");
        for (Rating rating : ratings) {
            System.out.println("  Username: " + rating.getUsername());
            System.out.println("  Rating: " + rating.getScore());
            System.out.println("  Comment: " + rating.getComments());
        }
        System.out.println("Plot Description: " + plotDescription);
        System.out.println("Average Rating: " + averageRating);
        System.out.println("Duration: " + duration + " minutes");
        System.out.println("Release Year: " + releaseYear);
        System.out.println("------------------------------");
    }
}

