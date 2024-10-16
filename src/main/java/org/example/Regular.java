package org.example;

import java.util.*;
import java.util.List;
import java.util.Scanner;

public class Regular extends User implements RequestsManager {

    public Set<String> reviewedProductions;
    public Regular(Information userInfo) {
        super(userInfo, AccountType.REGULAR);
        reviewedProductions = new HashSet<>();
    }

    public void addFavorite(Comparable<?> item) {
        favorites.add(item);
    }


    public void removeFavorite(Comparable<?> item) {
        favorites.remove(item);
    }

    public void addFavoriteProduction(Production production){
        favorites.add(production);
    }

    public void removeFavoriteProduction(Production production){
        favorites.remove(production);
    }

    @Override
    public void updateExperience() {
        experience += 1;
    }

    @Override
    public void logout() {
        System.out.println("Regular User logged out.");
    }

    @Override
    public void createRequest(Request request) {
        Scanner scanner = new Scanner(System.in);
        request.setCreatedByUsername(getUsername());

        List<Contributor> contributors = IMDB.getInstance().getContributors();
        List<Movie> movies = IMDB.getInstance().getMovies();
        List<Admin> admins = IMDB.getInstance().getAdmins();
        List<Actor> actors = IMDB.getInstance().getActors();

        System.out.println("Introduceti tipul de cerere:\n1.MOVIE_ISSUE\n2.ACTOR_ISSUE\n3.DELETE_ACCOUNT\n4.OTHERS");
        int issue = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Introduceti descrierea problemei");
        String problemDescription = scanner.nextLine();
        request.setProblemDescription(problemDescription);
        switch(issue){
            case 1:
                request.setRequestType(Request.RequestType.MOVIE_ISSUE);

                Movie searchMovie = null;
                for(Movie movie : movies){
                    if(problemDescription.contains(movie.getTitle())){
                        request.setTitleOrActorName(movie.getTitle());
                        searchMovie = movie;
                        break;
                    }
                }

                if(searchMovie == null){
                    System.out.println("Filmul nu a fost gasit.");
                }

                boolean ok = false;
                for(Contributor contributor : contributors){
                    if(contributor.userCreated.contains(searchMovie)){
                        request.setAssignedToUsername(contributor.getUsername());
                        contributor.assignedRequests.add(request);
                        request.registerObserver(contributor);
                        request.notifyObservers("Aveti o cerere noua", 1);
                        System.out.println("Cererea a fost adaugata cu succes!");
                        ok = true;
                        break;
                    }
                }
                for(Admin admin : admins){
                    if(admin.userCreated.contains(searchMovie)){
                        request.setAssignedToUsername(admin.getUsername());
                        request.registerObserver(admin);
                        request.notifyObservers("Aveti o cerere noua", 1);
                        admin.assignedRequests.add(request);
                        System.out.println("Cererea a fost adaugata cu succes!");
                        ok = true;
                        break;
                    }
                }
                if(ok == false){
                    System.out.println("Nu a fost gasit creatorul.");
                }
                break;
            case 2:
                request.setRequestType(Request.RequestType.ACTOR_ISSUE);

                Actor searchActor = null;
                for(Actor actor : actors){
                    if(problemDescription.contains(actor.getName())){
                        request.setTitleOrActorName(actor.getName());
                        searchActor = actor;
                        break;
                    }
                }

                if(searchActor == null){
                    System.out.println("Actorul nu a fost gasit.");
                }

                boolean ok1 = false;
                for(Contributor contributor : contributors){
                    if(contributor.userCreated.contains(searchActor)){
                        request.setAssignedToUsername(contributor.getUsername());
                        contributor.assignedRequests.add(request);
                        request.registerObserver(contributor);
                        request.notifyObservers("Aveti o cerere noua", 1);
                        System.out.println("Cererea a fost adaugata cu succes!");
                        ok1 = true;
                    }
                }
                for(Admin admin : admins){
                    if(admin.userCreated.contains(searchActor)){
                        request.setAssignedToUsername(admin.getUsername());
                        admin.assignedRequests.add(request);
                        request.registerObserver(admin);
                        request.notifyObservers("Aveti o cerere noua", 1);
                        System.out.println("Cererea a fost adaugata cu succes!");
                        ok1 = true;
                    }
                }
                if(ok1 == false){
                    System.out.println("Nu a fost gasit creatorul.");
                }
                break;
            case 3:
                request.setRequestType(Request.RequestType.DELETE_ACCOUNT);
                request.setAssignedToUsername("ADMIN");
                for(Admin admin : admins){
                    request.registerObserver(admin);
                    request.notifyObservers("Aveti o cerere noua", 1);
                    request.removeObserver(admin);
                }
                Admin.RequestHolder.addRequest(request);
                System.out.println("Cererea a fost adaugata cu succes!");
                break;
            case 4:
                request.setRequestType(Request.RequestType.OTHERS);
                request.setAssignedToUsername("ADMIN");
                for(Admin admin : admins){
                    request.registerObserver(admin);
                    request.notifyObservers("Aveti o cerere noua" +
                            "", 1);
                    request.removeObserver(admin);
                }
                Admin.RequestHolder.addRequest(request);
                System.out.println("Cererea a fost adaugata cu succes!");
                break;
            default:
                System.out.println("Opțiune invalidă. Încercați din nou.");
                break;
        }
    }

    @Override
    public void deleteRequest(Request request) {
        List<Contributor> contributors = IMDB.getInstance().getContributors();
        List<Admin> admins = IMDB.getInstance().getAdmins();
        if(request.getRequestType().equals(Request.RequestType.MOVIE_ISSUE) || request.getRequestType().equals(Request.RequestType.ACTOR_ISSUE)){
            for(Admin admin : admins){
                if(request.getAssignedToUsername().equals(admin.getUsername())){
                    admin.assignedRequests.remove(request);
                    System.out.println("Cererea a fost stearsa cu succes!.");
                    break;
                }
            }
            for(Contributor contributor : contributors){
                if(request.getAssignedToUsername().equals(contributor.getUsername())){
                    contributor.assignedRequests.remove(request);
                    System.out.println("Cererea a fost stearsa cu succes!.");
                    break;
                }
            }
        }else if(request.getRequestType().equals(Request.RequestType.DELETE_ACCOUNT) || request.getRequestType().equals(Request.RequestType.OTHERS)){
            Admin.RequestHolder.removeRequest(request);
            System.out.println("Cererea a fost stearsa cu succes!");
        }
    }

    public void addReview(String name, int score, String comments) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Alege optiunea:\n1.Film\n2.Serial");
        int recenzie = scanner.nextInt();
        scanner.nextLine();

        switch (recenzie) {
            case 1:
                List<Movie> movies = IMDB.getInstance().getMovies();
                Movie searchMovie = null;

                for (Movie movie : movies) {
                    if (movie.getTitle().equalsIgnoreCase(name)) {
                        searchMovie = movie;
                        break;
                    }
                }

                if (searchMovie != null) {
                    if (reviewedProductions.contains(searchMovie.getTitle())) {
                        System.out.println("Ai adaugat deja o recenzie pentru acest film. Doresti sa o stergi si sa adaugi una noua? (Da/Nu)");
                        String option = scanner.nextLine();
                        if (option.equalsIgnoreCase("Da")) {
                            searchMovie.removeRating(getUsername());
                            searchMovie.addRating(getUsername(), score, comments);
                            System.out.println("Recenzia a fost inlocuita cu succes pentru filmul: " + searchMovie.getTitle());
                        } else {
                            System.out.println("Recenzia existenta nu a fost inlocuita.");
                        }
                    } else {
                        searchMovie.addRating(getUsername(), score, comments);
                        reviewedProductions.add(searchMovie.getTitle());
                        System.out.println("Recenzie adaugata cu succes pentru filmul: " + searchMovie.getTitle());
                    }
                }
                break;

            case 2:
                List<Series> series = IMDB.getInstance().getSeries();
                Series searchSeries = null;

                for (Series serie : series) {
                    if (serie.getTitle().equalsIgnoreCase(name)) {
                        searchSeries = serie;
                        break;
                    }
                }

                if (searchSeries != null) {
                    if (reviewedProductions.contains(searchSeries.getTitle())) {
                        System.out.println("Ai adaugat deja o recenzie pentru acest serial. Doresti sa o stergi si sa adaugi una noua? (Da/Nu)");
                        String option = scanner.nextLine();
                        if (option.equalsIgnoreCase("Da")) {
                            searchSeries.removeRating(getUsername());
                            searchSeries.addRating(getUsername(), score, comments);
                            System.out.println("Recenzia a fost inlocuita cu succes pentru serialul: " + searchSeries.getTitle());
                        } else {
                            System.out.println("Recenzia existenta nu a fost inlocuita.");
                        }
                    } else {
                        searchSeries.addRating(getUsername(), score, comments);
                        reviewedProductions.add(searchSeries.getTitle());
                        System.out.println("Recenzie adaugata cu succes pentru serialul: " + searchSeries.getTitle());

                    }
                } else {
                    System.out.println("Serialul nu a fost gasit.");
                }
                break;

            default:
                System.out.println("Optiune invalida.");
                break;
        }
    }


    public List<String> getNotifications() {
        return userNotifications;
    }

    public void getFavorites(){
        System.out.println("Lista de actori favoriti:");
        for (Object favorite : favorites) {
            if (favorite instanceof Actor) {
                Actor favoriteActor = (Actor) favorite;
                System.out.println(favoriteActor);
            }
        }
    }
    public void getProductionFavorites() {
        System.out.println("Lista de productii favorite:");

        for (Object favorite : favorites) {
            if (favorite instanceof Production) {
                Production favoriteProduction = (Production) favorite;
                favoriteProduction.displayInfo();
            }
        }
    }


    public void update() {
        System.out.println("Ai primit o notificare");
    }

}
