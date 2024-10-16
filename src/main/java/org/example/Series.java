package org.example;

import java.util.*;

public class Series extends Production {

    private int releaseYear;
    private int numberOfSeasons;
    private Map<String, List<Episode>> episodesBySeason;

    public Series(String title, List<String> directors, List<String> actors, List<Genre> genres,
                  List<Rating> ratings, String plotDescription, int releaseYear, int numberOfSeasons) {
        super(title, directors, actors, genres, ratings, plotDescription);
        this.releaseYear = releaseYear;
        this.numberOfSeasons = numberOfSeasons;
        this.episodesBySeason = new HashMap<>();
    }

    public Series(){
        this.title = null;
        this.directors = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.plotDescription = null;
        this.releaseYear = 0;
        this.numberOfSeasons = 0;
        this.episodesBySeason = new TreeMap<>();
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public Map<String, List<Episode>> getEpisodesBySeason() {
        return episodesBySeason;
    }

    public void addEpisode(String seasonName, Episode episode) {
        episodesBySeason.computeIfAbsent(seasonName, k -> new LinkedList<>()).add(episode);
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public void setEpisodesBySeason(Map<String, List<Episode>> episodesBySeason) {
        this.episodesBySeason = episodesBySeason;
    }

    @Override
    public String toString() {
        return "Series{" +
                "releaseYear=" + releaseYear +
                ", numberOfSeasons=" + numberOfSeasons +
                ", episodesBySeason=" + episodesBySeason +
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
        System.out.println("Series Title: " + title);
        System.out.println("Directors: " + directors);
        System.out.println("Actors: " + actors);
        System.out.println("Genres: " + genres);
        for (Rating rating : ratings) {
            System.out.println("  Username: " + rating.getUsername());
            System.out.println("  Rating: " + rating.getScore());
            System.out.println("  Comment: " + rating.getComments());
        }
        System.out.println("Plot Description: " + plotDescription);
        System.out.println("Average Rating: " + averageRating);
        System.out.println("Release Year: " + releaseYear);
        System.out.println("Number of Seasons: " + numberOfSeasons);

        // Display Episodes by Season
        for (Map.Entry<String, List<Episode>> entry : episodesBySeason.entrySet()) {
            String seasonName = entry.getKey();
            List<Episode> episodes = entry.getValue();

            System.out.println("Season: " + seasonName);
            for (Episode episode : episodes) {
                System.out.println("  Episode Name: " + episode.getEpisodeName());
                System.out.println("  Duration: " + episode.getDuration() + " minutes");
            }
        }

        System.out.println("------------------------------");
    }
}
