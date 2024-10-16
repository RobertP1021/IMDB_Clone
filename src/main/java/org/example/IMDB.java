package org.example;
import java.security.SecureRandom;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;

public class IMDB {
    private static IMDB instance;

    public ActorExperience actorExperience;
    public IssueExperience issueExperience;
    public ProductionExperience productionExperience;
    public RatingExperience ratingExperience;
    public  List<Regular> regularUsers;
    public  List<Contributor> contributors;
    public List<Admin> admins;
    public  List<Actor> actors;
    public List<Request> requests;
    public List<Movie> movies;
    public List<Series> series;


    public IMDB(List<Regular> regularUsers, List<Contributor> contributors, List<Admin> admins,
                List<Actor> actors, List<Request> requests, List<Movie> movies, List<Series> series) {
        this.regularUsers = regularUsers;
        this.contributors = contributors;
        this.admins = admins;
        this.actors = actors;
        this.requests = requests;
        this.movies = movies;
        this.series = series;
        actorExperience = new ActorExperience();
        ratingExperience = new RatingExperience();
        issueExperience = new IssueExperience();
        productionExperience = new ProductionExperience();
    }

    public static IMDB getInstance() {
        if (instance == null) {
            instance = new IMDB(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
        return instance;
    }
    public void run() {
        loadTestData();

        System.out.println("Bine ați venit în IMDB!");

        // Display login options
        System.out.println("Alegeți modul de autentificare:");
        System.out.println("1. Terminal");
        System.out.println("2. Interfață grafică");

        Scanner scanner = new Scanner(System.in);
        int authChoice = scanner.nextInt();
        scanner.nextLine();

        switch (authChoice) {
            case 1:
                while(true){
                    User currentUser = authenticateUser();
                    if (currentUser == null) {
                        System.out.println("Autentificare eșuată. Aplicația se închide.");
                        return;
                    }

                    if (currentUser instanceof Regular) {
                        handleRegularUserFlow((Regular) currentUser, scanner);
                    } else if (currentUser instanceof Contributor) {
                        handleContributorFlow((Contributor) currentUser, scanner);
                    } else if (currentUser instanceof Admin) {
                        handleAdminFlow((Admin) currentUser, scanner);
                    }
                }
            case 2:
                SwingLogin swingLogin = new SwingLogin(this);
                swingLogin.showLoginFrame();
                break;
            default:
                System.out.println("Opțiune invalidă. Se închide aplicația.");
        }
    }
//    public void run() {
//        loadTestData();
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Bine ați venit în IMDB!");
//
//        while(true){
//            User currentUser = authenticateUser();
//            if (currentUser == null) {
//                System.out.println("Autentificare eșuată. Aplicația se închide.");
//                return;
//            }
//
//            if (currentUser instanceof Regular) {
//                handleRegularUserFlow((Regular) currentUser, scanner);
//            } else if (currentUser instanceof Contributor) {
//                handleContributorFlow((Contributor) currentUser, scanner);
//            } else if (currentUser instanceof Admin) {
//                handleAdminFlow((Admin) currentUser, scanner);
//            }
//        }
//    }

    private void loadTestData() {
        loadActorsFromJson("D:\\POO-TEMA-2023-CHECKER-NOU (1)\\POO-TEMA-2023-CHECKER\\src\\main\\resources\\input\\actors.json");
        loadProductionFromJson("D:\\POO-TEMA-2023-CHECKER-NOU (1)\\POO-TEMA-2023-CHECKER\\src\\main\\resources\\input\\production.json");
        loadUsersFromJson("D:\\POO-TEMA-2023-CHECKER-NOU (1)\\POO-TEMA-2023-CHECKER\\src\\main\\resources\\input\\accounts.json");
        loadRequestsFromJson("D:\\POO-TEMA-2023-CHECKER-NOU (1)\\POO-TEMA-2023-CHECKER\\src\\main\\resources\\input\\requests.json");

    }

    private void loadRequestsFromJson(String filePath) {
        try {
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(filePath);
            Object obj = jsonParser.parse(reader);
            JSONArray requestsArray = (JSONArray) obj;

            for (Object requestObj : requestsArray) {
                JSONObject requestJson = (JSONObject) requestObj;

                String typeString = (String) requestJson.get("type");
                Request.RequestType requestType = Request.RequestType.valueOf(typeString);

                String createdDateString = (String) requestJson.get("createdDate");
                LocalDateTime creationDate = LocalDateTime.parse(createdDateString);

                String username = (String) requestJson.get("username");
                String to = (String) requestJson.get("to");
                String assignedToUsername = determineAssignedToUsername(requestType, to);

                String description = (String) requestJson.get("description");

                Request request = new Request(requestType, null, description, username, assignedToUsername);
                request.setCreationDate(creationDate);

                for(Regular regular : regularUsers){
                    if(regular.getUsername().equals(username)){
                        request.registerObserver(regular);
                        break;
                    }
                }
                for(Contributor contributor : contributors){
                    if(contributor.getUsername().equals(username)){
                        request.registerObserver(contributor);
                        break;
                    }
                }

                switch (requestType) {
                    case DELETE_ACCOUNT:
                        request.setAssignedToUsername("ADMIN");
                        for(Admin admin : admins){
                            request.registerObserver(admin);
                            request.notifyObservers("Aveti o cerere noua", 2);
                            request.removeObserver(admin);
                        }
                        Admin.RequestHolder.addRequest(request);
                        break;
                    case ACTOR_ISSUE:
                        String actorName = (String) requestJson.get("actorName");
                        request.setTitleOrActorName(actorName);
                        Actor searchActor = null;
                        for(Actor actor : actors){
                            if(description.contains(actor.getName())){
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
                                request.notifyObservers("Aveti o cerere noua", 2);
                                ok1 = true;
                                break;
                            }
                        }
                        for(Admin admin : admins){
                            if(admin.userCreated.contains(searchActor)){
                                request.setAssignedToUsername(admin.getUsername());
                                admin.assignedRequests.add(request);
                                request.registerObserver(admin);
                                request.notifyObservers("Aveti o cerere noua", 2);
                                ok1 = true;
                                break;
                            }
                        }
                        if(ok1 == false){
                            System.out.println("Nu a fost gasit creatorul.");
                        }
                        break;
                    case MOVIE_ISSUE:
                        String movieTitle = (String) requestJson.get("movieTitle");
                        request.setTitleOrActorName(movieTitle);
                        Movie searchMovie = null;
                        for(Movie movie : movies){
                            if(description.contains(movie.getTitle())){
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
                                request.notifyObservers("Aveti o cerere noua", 2);
                                ok = true;
                                break;
                            }
                        }
                        for(Admin admin : admins){
                            if(admin.userCreated.contains(searchMovie)){
                                request.setAssignedToUsername(admin.getUsername());
                                request.registerObserver(admin);
                                request.notifyObservers("Aveti o cerere noua", 2);
                                admin.assignedRequests.add(request);
                                ok = true;
                                break;
                            }
                        }
                        if(ok == false){
                            System.out.println("Nu a fost gasit creatorul.");
                        }
                        break;
                    case OTHERS:
                        request.setAssignedToUsername("ADMIN");
                        for(Admin admin : admins){
                            request.registerObserver(admin);
                            request.notifyObservers("Aveti o cerere noua" +
                                    "", 2);
                            request.removeObserver(admin);
                        }
                        Admin.RequestHolder.addRequest(request);
                        break;
                }

                requests.add(request);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private String determineAssignedToUsername(Request.RequestType requestType, String to) {
        switch (requestType) {
            case DELETE_ACCOUNT:
            case OTHERS:
                return "ADMIN";
            case ACTOR_ISSUE:
            case MOVIE_ISSUE:
                return to;
            default:
                return null;
        }
    }
    private void loadUsersFromJson(String filePath) {
        try {
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(filePath);
            Object obj = jsonParser.parse(reader);
            JSONArray arrayMare = (JSONArray) obj;

            for (int i = 0; i < arrayMare.size(); i++) {
                JSONObject accountsList = (JSONObject) arrayMare.get(i);
                String username = (String) accountsList.get("username");

                int experience;
                if (accountsList.get("experience") == null) {
                    experience = 0;
                } else {
                    experience = Integer.parseInt((String) accountsList.get("experience"));
                }
                JSONObject informatii = (JSONObject) accountsList.get("information");
                JSONObject credentiale = (JSONObject) informatii.get("credentials");
                String email = (String) credentiale.get("email");
                String parola = (String) credentiale.get("password");
                String name = (String) informatii.get("name");
                String country = (String) informatii.get("country");
                long age = (long) informatii.get("age");
                char gender = ((String) informatii.get("gender")).charAt(0);
                String birthDateString = (String) informatii.get("birthDate");
                LocalDate birthDate = LocalDate.parse(birthDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDateTime convertedBirthDate = birthDate.atStartOfDay();
                JSONArray notificationsArray = (JSONArray) accountsList.get("notifications");

                String userType = (String) accountsList.get("userType");
                User.Information userInfo = new User.InformationBuilder()
                        .credentials(new Credentials(email, parola))
                        .name(name)
                        .country(country)
                        .age((int) age)
                        .gender(gender)
                        .birthDate(convertedBirthDate)
                        .build();

                AccountType accountType;
                switch (userType) {
                    case "Contributor":
                        accountType = AccountType.CONTRIBUTOR;
                        break;
                    case "Regular":
                        accountType = AccountType.REGULAR;
                        break;
                    case "Admin":
                        accountType = AccountType.ADMIN;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid user type");
                }

                User user = UserFactory.createUser(userInfo, accountType);
                user.setUsername(username);
                user.setExperience(experience);

                switch (accountType) {
                    case ADMIN:
                        admins.add((Admin) user);
                        break;
                    case CONTRIBUTOR:
                        contributors.add((Contributor) user);
                        break;
                    case REGULAR:
                        regularUsers.add((Regular) user);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid account type");
                }
                if (notificationsArray != null) {
                    for (Object notificationObj : notificationsArray) {
                        String notification = (String) notificationObj;
                        user.userNotifications.add(notification);
                    }
                }
                JSONArray favoriteProductionsArray = (JSONArray) accountsList.get("favoriteProductions");
                if (favoriteProductionsArray != null) {
                    for (Object productionObj : favoriteProductionsArray) {
                        String favoriteProductionTitle = (String) productionObj;
                        for(Movie movie : movies){
                            if(movie.getTitle().equals(favoriteProductionTitle)){
                                user.addFavoriteProduction(movie);
                            }
                        }
                        for(Series serie : series){
                            if(serie.getTitle().equals(favoriteProductionTitle)){
                                user.addFavoriteProduction(serie);
                            }
                        }
                    }
                }

                JSONArray favoriteActorsArray = (JSONArray) accountsList.get("favoriteActors");
                if (favoriteActorsArray != null) {
                    for (Object actorObj : favoriteActorsArray) {
                        String favoriteActorName = (String) actorObj;
                        for(Actor actor : actors){
                            if(actor.getName().equals(favoriteActorName)){
                                user.addFavorite(actor);
                            }
                        }
                    }
                }
                JSONArray productionsContributionArray = (JSONArray) accountsList.get("productionsContribution");
                if (productionsContributionArray != null) {
                    for (Object productionObj : productionsContributionArray) {
                        String contributedProductionTitle = (String) productionObj;
                        for(Movie movie : movies){
                            if(movie.getTitle().equals(contributedProductionTitle)){
                                ((Staff)user).userCreated.add(movie);
                            }
                        }
                        for(Series serie : series){
                            if(serie.getTitle().equals(contributedProductionTitle)){
                                ((Staff)user).userCreated.add(serie);
                            }
                        }
                    }
                }

                JSONArray actorsContributionArray = (JSONArray) accountsList.get("actorsContribution");
                if (actorsContributionArray != null) {
                    for (Object actorObj : actorsContributionArray) {
                        String contributedActorName = (String) actorObj;
                        for(Actor actor : actors){
                            if(actor.getName().equals(contributedActorName)){
                                ((Staff)user).userCreated.add(actor);
                            }
                        }
                    }
                }

            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadProductionFromJson(String filePath) {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(filePath)) {
            Object objection = parser.parse(reader);

            JSONArray productionsArray = (JSONArray) objection;

            for (Object productionObj : productionsArray) {
                JSONObject productionJson = (JSONObject) productionObj;

                String title = (String) productionJson.get("title");
                String type = (String) productionJson.get("type");

                JSONArray directorsArray = (JSONArray) productionJson.get("directors");
                List<String> directors = new ArrayList<>();
                for (Object directorObj : directorsArray) {
                    directors.add((String) directorObj);
                }

                JSONArray actorsArray = (JSONArray) productionJson.get("actors");
                List<String> actors = new ArrayList<>();
                for (Object actorObj : actorsArray) {
                    actors.add((String) actorObj);
                }

                JSONArray genresArray = (JSONArray) productionJson.get("genres");
                List<Genre> genres = new ArrayList<>();
                for (Object genreObj : genresArray) {
                    genres.add(Genre.valueOf((String) genreObj));
                }

                List<Rating> ratings = new ArrayList<>();
                if (productionJson.containsKey("ratings")) {
                    JSONArray ratingsArray = (JSONArray) productionJson.get("ratings");
                    for (Object ratingObj : ratingsArray) {
                        JSONObject ratingJson = (JSONObject) ratingObj;
                        String username = (String) ratingJson.get("username");
                        int ratingValue = ratingJson.get("rating") != null ?
                                ((Long) ratingJson.get("rating")).intValue() : 0;
                        String comment = (String) ratingJson.get("comment");

                        Rating rating = new Rating(username, ratingValue, comment);
                        ratings.add(rating);
                    }
                }

                String plot = (String) productionJson.get("plot");
                double averageRating = (double) productionJson.get("averageRating");

                if (type.equals("Movie")) {
                    String duration = (String) productionJson.get("duration");
                    int releaseYear = productionJson.get("releaseYear") != null ?
                            ((Long) productionJson.get("releaseYear")).intValue() : 0;

                    Movie movie = new Movie(title, directors, actors, genres, ratings, plot, duration, releaseYear);
                    movies.add(movie);
                } else if ("Series".equals(type)) {
                    int releaseYear = productionJson.get("releaseYear") != null ?
                            ((Long) productionJson.get("releaseYear")).intValue() : 0;

                    int numSeasons = productionJson.get("numSeasons") != null ?
                            ((Long) productionJson.get("numSeasons")).intValue() : 0;

                    Series serie = new Series(title, directors, actors, genres, ratings, plot, releaseYear, numSeasons);

                    JSONObject seasonsJson = (JSONObject) productionJson.get("seasons");
                    for (Object seasonNameObj : seasonsJson.keySet()) {
                        String seasonName = (String) seasonNameObj;
                        JSONArray episodesArray = (JSONArray) seasonsJson.get(seasonName);

                        for (Object episodeObj : episodesArray) {
                            JSONObject episodeJson = (JSONObject) episodeObj;
                            String episodeName = (String) episodeJson.get("episodeName");
                            String episodeDuration = (String) episodeJson.get("duration");

                            Episode episode = new Episode(episodeName, episodeDuration);
                            serie.addEpisode(seasonName, episode);
                        }
                    }

                    series.add(serie);
                }
            }

        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadActorsFromJson(String filePath) {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(filePath)) {
            Object objection = parser.parse(reader);

            JSONArray actorsArray = (JSONArray) objection;

            for (Object actorObj : actorsArray) {
                JSONObject actorJson = (JSONObject) actorObj;

                String name = (String) actorJson.get("name");
                String biography = (String) actorJson.get("biography");

                List<Actor.NameTypePair> performances = new ArrayList<>();
                if (actorJson.containsKey("performances")) {
                    JSONArray performancesArray = (JSONArray) actorJson.get("performances");
                    for (Object performanceObj : performancesArray) {
                        JSONObject performanceJson = (JSONObject) performanceObj;
                        String performanceTitle = (String) performanceJson.get("title");
                        String performanceType = (String) performanceJson.get("type");

                        Actor.NameTypePair performance = new Actor.NameTypePair(performanceTitle, performanceType);
                        performances.add(performance);
                    }
                }

                Actor actor = new Actor(name, performances, biography);
                actors.add(actor);
            }

        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    public User authenticateUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduceți email: ");
        String username = scanner.nextLine();
        System.out.print("Introduceți parola: ");
        String password = scanner.nextLine();

        try {
            for (Regular regular : regularUsers) {
                if (regular.getInformation().getCredentials().getEmail().equalsIgnoreCase(username)
                        && regular.getInformation().getCredentials().getPassword().equals(password)) {
                    return regular;
                }
            }

            for (Contributor contributor : contributors) {
                if (contributor.getInformation().getCredentials().getEmail().equalsIgnoreCase(username)
                        && contributor.getInformation().getCredentials().getPassword().equals(password)) {
                    return contributor;
                }
            }

            for (Admin admin : admins) {
                if (admin.getInformation().getCredentials().getEmail().equalsIgnoreCase(username)
                        && admin.getInformation().getCredentials().getPassword().equals(password)) {
                    return admin;
                }
            }

            throw new InvalidCommandException("Utilizatorul nu a fost găsit.");
        } catch (InvalidCommandException e) {
            System.out.println(e.getMessage());
            return authenticateUser();
        }
    }

    private void handleRegularUserFlow(Regular regularUser, Scanner scanner) {
        boolean loggedIn = true;
        System.out.println("Bine ați venit, " + regularUser.getUsername() + "!");

        while (loggedIn) {
            displayOptionsRegular();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Alegeți opțiunea dorită:");
                    System.out.println("1. Afișează toate producțiile");
                    System.out.println("2. Filtrează producțiile după gen");

                    int option = scanner.nextInt();
                    scanner.nextLine();

                    switch (option) {
                        case 1:
                            System.out.println("Detalii despre toate producțiile din sistem:");

                            System.out.println("Movies:");
                            for (Movie movie : movies) {
                                movie.displayInfo();
                            }

                            System.out.println("Series:");
                            for (Series series : series) {
                                series.displayInfo();
                            }
                            break;

                        case 2:
                            System.out.println("Introduceti un gen pentru a filtra rezultatele (introduceti 'done' cand ati terminat):");
                            String filterGenreStr;
                            while (!(filterGenreStr = scanner.nextLine()).equalsIgnoreCase("done")) {
                                try {
                                    Genre filterGenre = Genre.valueOf(filterGenreStr);

                                    System.out.println("Movies with genre " + filterGenre + ":");
                                    for (Movie movie : movies) {
                                        if (movie.getGenres().contains(filterGenre)) {
                                            movie.displayInfo();
                                        }
                                    }

                                    System.out.println("Series with genre " + filterGenre + ":");
                                    for (Series seriesItem : series) {
                                        if (seriesItem.getGenres().contains(filterGenre)) {
                                            seriesItem.displayInfo();
                                        }
                                    }

                                } catch (IllegalArgumentException e) {
                                    System.out.println("Genul introdus nu este valid. Incercati din nou.");
                                }
                            }
                            break;

                        default:
                            System.out.println("Opțiunea introdusă nu este validă. Adaugarea a fost anulată.");
                            break;
                    }
                    break;
                case 2:
                    System.out.println("Doriti sa sortati dupa nume?\n1.Da\n2.Nu");
                    int sort = scanner.nextInt();
                    scanner.nextLine();
                    switch(sort){
                        case 1:
                            List<Actor> sortedActors = new ArrayList<>(actors);
                            sortedActors.sort(Comparator.comparing(Actor::getName));

                            for (Actor actor : sortedActors) {
                                System.out.println(actor);
                            }
                            break;
                        case 2:
                            for (Actor actor : actors) {
                                System.out.println(actor);
                            }
                            break;
                        default:
                            System.out.println("Optiune invalida");
                            break;
                    }
                    break;
                case 3:
                    if(regularUser.getNotifications().isEmpty()){
                        System.out.println("\nNu aveti notificari.\n");
                    }else{
                        System.out.println("");
                        regularUser.getNotifications().forEach(System.out::println);
                        System.out.println("");
                    }
                    break;
                case 4:
                    System.out.println("1.Actor\n2.Serial\n3.Film");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    switch(choice) {
                        case 1:
                            System.out.println("Introduceți numele actorului:");
                            String actorName = scanner.nextLine();

                            Actor searchActor = null;
                            for (Actor actor : actors) {
                                if (actor.getName().toLowerCase().contains(actorName.toLowerCase())) {
                                    searchActor = actor;
                                    break;
                                }
                            }

                            if (searchActor != null) {
                                System.out.println(searchActor);

                            } else {
                                System.out.println("Actorul nu a fost găsit.");
                            }
                            break;
                        case 2:
                            break;
                        case 3:
                            System.out.println("Introduceți titlul filmului:");
                            String movieTitle = scanner.nextLine();

                            Movie searchMovie = null;
                            for (Movie movie : movies) {
                                if (movie.getTitle().toLowerCase().contains(movieTitle.toLowerCase())) {
                                    searchMovie = movie;
                                    break;
                                }
                            }

                            if (searchMovie != null) {
                                searchMovie.displayInfo();
                            } else {
                                System.out.println("Filmul nu a fost găsit.");
                            }
                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;

                    }
                    break;
                case 5:
                    System.out.println("Alegeti optiunea:\n1.Adaugare\n2.Stergere");
                    int alegere = scanner.nextInt();
                    scanner.nextLine();
                    switch(alegere){
                        case 1:
                            System.out.println("Alegeti optiunea:\n1.Adaugare Actor\n2.Adaugare Productie");
                            int adaugare = scanner.nextInt();
                            scanner.nextLine();
                            switch(adaugare){
                                case 1:
                                    for (Actor actor : actors) {
                                        System.out.println(actor);
                                    }
                                    System.out.println("Introduceti numele actorului");
                                    String actorName = scanner.nextLine();
                                    Actor searchActor = null;

                                    for (Actor actor : actors) {
                                        if (actor.getName().equalsIgnoreCase(actorName)) {
                                            searchActor = actor;
                                            break;
                                        }
                                    }
                                    if(searchActor != null){
                                        regularUser.addFavorite(searchActor);
                                        regularUser.getFavorites();
                                    }else{
                                        System.out.println("Actorul nu a fost gasit.");
                                    }
                                    break;
                                case 2:
                                    System.out.println("Alegeti optiunea:\n1.Adaugare Film\n2.Adaugare Serial");
                                    int productie = scanner.nextInt();
                                    scanner.nextLine();
                                    switch(productie){
                                        case 1:
                                            for(Movie movie : movies){
                                                movie.displayInfo();
                                            }
                                            System.out.println("Introduceti numele filmului:");
                                            String filmName = scanner.nextLine();
                                            Movie existingMovie = null;

                                            for(Movie movie : movies){
                                                if(movie.getTitle().equalsIgnoreCase(filmName)){
                                                    existingMovie = movie;
                                                    break;
                                                }

                                            }

                                            if(existingMovie != null){
                                                regularUser.addFavoriteProduction(existingMovie);
                                                regularUser.getProductionFavorites();
                                            }else{
                                                System.out.println("Filmul nu a fost gasit.");
                                            }
                                            break;
                                        case 2:
                                            for (Series series : series) {
                                                series.displayInfo();
                                            }
                                            System.out.println("Introduceti numele serialului:");
                                            String seriesName = scanner.nextLine();
                                            Series existingSeries = null;

                                            for (Series series : series) {
                                                if (series.getTitle().equalsIgnoreCase(seriesName)) {
                                                    existingSeries = series;
                                                    break;
                                                }
                                            }

                                            if (existingSeries != null) {
                                                regularUser.addFavoriteProduction(existingSeries);
                                                regularUser.getProductionFavorites();
                                            } else {
                                                System.out.println("Serialul nu a fost gasit.");
                                            }

                                            break;
                                        default:
                                            System.out.println("Opțiune invalidă. Încercați din nou.");
                                            break;
                                    }
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        case 2:
                            System.out.println("Alegeti optiunea:\n1.Stergere Actor\n2.Stergere Productie");
                            int stergere = scanner.nextInt();
                            scanner.nextLine();
                            switch(stergere){
                                case 1:
                                    regularUser.getFavorites();
                                    System.out.println("Introduceti numele actorului:");
                                    String actorName = scanner.nextLine();

                                    boolean actorRemoved = false;
                                    for (Object favorite : regularUser.favorites) {
                                        if (favorite instanceof Actor && ((Actor) favorite).getName().equalsIgnoreCase(actorName)) {
                                            regularUser.removeFavorite((Comparable<?>) favorite);
                                            actorRemoved = true;
                                            System.out.println("Actorul a fost sters cu succes!");
                                            break;
                                        }
                                    }

                                    if (!actorRemoved) {
                                        System.out.println("Actorul nu a fost gasit in lista de favorite.");
                                    }

                                    regularUser.getFavorites();
                                    break;
                                case 2:
                                    regularUser.getProductionFavorites();
                                    System.out.println("Introduceti numele productiei:");
                                    String productionName = scanner.nextLine();

                                    boolean productionRemoved = false;
                                    for (Object favorite : regularUser.favorites) {
                                        if (favorite instanceof Production && ((Production) favorite).getTitle().equalsIgnoreCase(productionName)) {
                                            regularUser.removeFavoriteProduction((Production) favorite);
                                            productionRemoved = true;
                                            System.out.println("Productia a fost stearsa cu succes!");
                                            break;
                                        }
                                    }

                                    if (!productionRemoved) {
                                        System.out.println("Productia nu a fost gasita in lista de favorite.");
                                    }

                                    regularUser.getProductionFavorites();
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }

                    break;
                case 6:
                    System.out.println("Alegeti optiunea:\n1.Creare cerere\n2.Stergere cerere");
                    int cerere = scanner.nextInt();
                    scanner.nextLine();
                    switch(cerere){
                        case 1:
                            Request request = new Request();
                            regularUser.createRequest(request);
                            requests.add(request);
                            request.registerObserver(regularUser);
                            break;
                        case 2:
                            int index = 1;
                            for (Request request1 : requests) {
                                if (request1.getCreatedByUsername().equals(regularUser.getUsername())) {
                                    System.out.println(index + ". " + request1);
                                    index++;
                                }
                            }

                            if(index != 1){
                                System.out.print("Introduceți numărul cererii pe care doriți să o ștergeți: ");
                                int alegere1 = scanner.nextInt();
                                scanner.nextLine();

                                if (alegere1 > 0 && alegere1 <= index) {
                                    Request cerereSelectata = null;
                                    index = 1;
                                    for (Request request1 : requests) {
                                        if (request1.getCreatedByUsername().equals(regularUser.getUsername())) {
                                            if (index == alegere1) {
                                                cerereSelectata = request1;
                                                break;
                                            }
                                            index++;
                                        }
                                    }

                                    if (cerereSelectata != null) {
                                        regularUser.deleteRequest(cerereSelectata);
                                        requests.remove(cerereSelectata);
                                        cerereSelectata.removeObserver(regularUser);
                                    } else {
                                        System.out.println("Eroare la selectarea cererii.");
                                    }
                                } else {
                                    System.out.println("Indexul cererii este invalid.");
                                }
                            }else{
                                System.out.println("Nu aveti cereri pe care sa le stergeti.");
                            }

                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }
                    break;
                case 7:
                    System.out.println("Alegeti optiunea:\n1.Adaugare recenzie\n2.Stergere recenzie");
                    int recenzie = scanner.nextInt();
                    scanner.nextLine();
                    switch(recenzie){
                        case 1:
                            System.out.println("Introduceti numele productiei:");
                            String productionName = scanner.nextLine();
                            System.out.println("Introduceti scorul pe care doriti sa l acordati:");
                            int score = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("Introduceti commentariul pe care doriti sa l lasati:");
                            String comment = scanner.nextLine();
                            for(Movie movie : movies){
                                for(Rating rating : movie.ratings){
                                    for(Regular regular : regularUsers){
                                        if(regular.getUsername().equals(rating.getUsername())){
                                            rating.registerObserver(regular);
                                            rating.notifyObservers("O recenzie noua a fost adaugata pentru filmul: " + movie.getTitle(), 1);
                                        }
                                    }
                                }
                            }

                            for (Series serie : series){
                                for(Rating rating : serie.ratings){
                                    for(Regular regular : regularUsers){
                                        if(regular.getUsername().equals(rating.getUsername())){
                                            rating.registerObserver(regular);
                                            rating.notifyObservers("O recenzie noua a fost adaugata pentru serialul: " + serie.getTitle(), 1);
                                        }
                                    }
                                }
                            }
                            Movie movieSearch = null;

                            for (Movie movie : movies) {
                                if (movie.getTitle().equalsIgnoreCase(productionName)) {
                                    movieSearch = movie;
                                    break;
                                }
                            }
                            Series seriesSearch = null;

                            for (Series serie : series) {
                                if (serie.getTitle().equalsIgnoreCase(productionName)) {
                                    seriesSearch = serie;
                                    break;
                                }
                            }
                           if(movieSearch != null){
                               if(regularUser.reviewedProductions.contains(movieSearch.getTitle())){
                                   break;
                               }else{
                                   regularUser.experience = ratingExperience.calculateExperience(regularUser);
                               }
                           }
                           if(seriesSearch != null){
                               if(regularUser.reviewedProductions.contains(seriesSearch.getTitle())){
                                   break;
                               }else{
                                   regularUser.experience = ratingExperience.calculateExperience(regularUser);
                               }
                           }
                            regularUser.addReview(productionName, score, comment);
                           if(movieSearch != null){
                               Rating rating = new Rating();
                               for(Admin admin : admins){
                                   if(admin.userCreated.contains(movieSearch)){
                                       rating.registerObserver(admin);
                                       rating.notifyObservers("O recenzie noua a fost adaugata pentru filmul adaugat de dumneavoastra: " +movieSearch.getTitle(), 1);
                                       break;
                                   }
                               }
                               for(Contributor contributor : contributors) {
                                   if (contributor.userCreated.contains(movieSearch)) {
                                       rating.registerObserver(contributor);
                                       rating.notifyObservers("O recenzie noua a fost adaugata pentru filmul adaugat de dumneavoastra: " + movieSearch.getTitle(), 1);
                                       break;
                                   }
                               }

                           }else{
                               System.out.println("Filmul nu a fost gasit");
                           }
                           if(seriesSearch != null){
                               Rating rating = new Rating();
                               for(Admin admin : admins){
                                   if(admin.userCreated.contains(seriesSearch)){
                                       rating.registerObserver(admin);
                                       rating.notifyObservers("O recenzie noua a fost adaugata pentru filmul adaugat de dumneavoastra: " + seriesSearch.getTitle(), 1);
                                       break;
                                   }
                               }
                               for(Contributor contributor : contributors) {
                                   if (contributor.userCreated.contains(seriesSearch)) {
                                       rating.registerObserver(contributor);
                                       rating.notifyObservers("O recenzie noua a fost adaugata pentru filmul adaugat de dumneavoastra: " + seriesSearch.getTitle(), 1);
                                       break;
                                   }
                               }
                           }
                            System.out.println("Experienta dumneavoastra: " + regularUser.experience);
                            break;
                        case 2:
                            Movie searchMovie = null;
                            for(Movie movie : movies){
                                if(regularUser.reviewedProductions.contains(movie.getTitle())){
                                    for(Rating rating : movie.ratings){
                                        if(rating.getUsername().equals(regularUser.getUsername())){
                                            System.out.println(movie.getTitle() + ": " + rating);
                                        }
                                    }
                                    searchMovie = movie;
                                }
                            }
                            if(searchMovie == null){
                                System.out.println("Nu aveti nicio recenzie la vreun film.");
                            }
                            Series searchSeries = null;
                            for(Series serie : series){
                                if(regularUser.reviewedProductions.contains(serie.getTitle())){
                                    for(Rating rating : serie.ratings){
                                        if(rating.getUsername().equals(regularUser.getUsername())){
                                            System.out.println(serie.getTitle() + rating);
                                        }
                                    }
                                    searchSeries = serie;
                                }
                            }
                            if(searchSeries == null){
                                System.out.println("Nu aveti nicio recenzie la vreun serial.");
                            }
                            System.out.println("Alegeti productia la care doriti sa stergeti recenzia:");
                            String name = scanner.nextLine();
                            for(Movie movie : movies){
                                if(movie.getTitle().toLowerCase().contains(name.toLowerCase())){
                                    for(Rating rating : movie.ratings){
                                        if(regularUser.getUsername().equals(rating.getUsername())){
                                            rating.removeObserver(regularUser);
                                        }
                                    }
                                    movie.removeRating(regularUser.getUsername());
                                    break;
                                }
                            }
                            for(Series serie : series){
                                if(serie.getTitle().toLowerCase().contains(name.toLowerCase())){
                                    for(Rating rating : serie.ratings){
                                        if(regularUser.getUsername().equals(rating.getUsername())){
                                            rating.removeObserver(regularUser);
                                        }
                                    }
                                    serie.removeRating(regularUser.getUsername());
                                    break;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case 8:
                    System.out.println("Alegeti optiunea:\n1.Delogare\n2.Inchidere program");
                    int logoutChoice = scanner.nextInt();
                    scanner.nextLine();
                    switch(logoutChoice){
                        case 1:
                            loggedIn = false;
                            break;
                        case 2:
                            System.out.println("Programul se închide. La revedere!");
                            System.exit(0);
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }
                    break;
                default:
                    System.out.println("Opțiune invalidă. Încercați din nou.");
                    break;
            }
        }
    }

    private void handleContributorFlow(Contributor contributor, Scanner scanner) {
        boolean loggedIn = true;
        System.out.println("Bine ați venit, " + contributor.getUsername() + "!");
        while (loggedIn) {
            displayOptionsContributor();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Alegeți opțiunea dorită:");
                    System.out.println("1. Afișează toate producțiile");
                    System.out.println("2. Filtrează producțiile după gen");

                    int option = scanner.nextInt();
                    scanner.nextLine();

                    switch (option) {
                        case 1:
                            System.out.println("Detalii despre toate producțiile din sistem:");

                            System.out.println("Movies:");
                            for (Movie movie : movies) {
                                movie.displayInfo();
                            }

                            System.out.println("Series:");
                            for (Series series : series) {
                                series.displayInfo();
                            }
                            break;

                        case 2:
                            System.out.println("Introduceti un gen pentru a filtra rezultatele (introduceti 'done' cand ati terminat):");
                            String filterGenreStr;
                            while (!(filterGenreStr = scanner.nextLine()).equalsIgnoreCase("done")) {
                                try {
                                    Genre filterGenre = Genre.valueOf(filterGenreStr);

                                    System.out.println("Movies with genre " + filterGenre + ":");
                                    for (Movie movie : movies) {
                                        if (movie.getGenres().contains(filterGenre)) {
                                            movie.displayInfo();
                                        }
                                    }

                                    System.out.println("Series with genre " + filterGenre + ":");
                                    for (Series seriesItem : series) {
                                        if (seriesItem.getGenres().contains(filterGenre)) {
                                            seriesItem.displayInfo();
                                        }
                                    }

                                } catch (IllegalArgumentException e) {
                                    System.out.println("Genul introdus nu este valid. Incercati din nou.");
                                }
                            }
                            break;

                        default:
                            System.out.println("Opțiunea introdusă nu este validă. Adaugarea a fost anulată.");
                            break;
                    }
                    break;
                case 2:
                    System.out.println("Doriti sa sortati dupa nume?\n1.Da\n2.Nu");
                    int sort = scanner.nextInt();
                    scanner.nextLine();
                    switch(sort){
                        case 1:
                            List<Actor> sortedActors = new ArrayList<>(actors);
                            sortedActors.sort(Comparator.comparing(Actor::getName));

                            for (Actor actor : sortedActors) {
                                System.out.println(actor);
                            }
                            break;
                        case 2:
                            for (Actor actor : actors) {
                                System.out.println(actor);
                            }
                            break;
                        default:
                            System.out.println("Optiune invalida");
                            break;
                    }
                    break;
                case 3:
                    if(contributor.getNotifications().isEmpty()){
                        System.out.println("\nNu aveti notificari.\n");
                    }else{
                        System.out.println("");
                        contributor.getNotifications().forEach(System.out::println);
                        System.out.println("");
                    }
                    break;
                case 4:
                    System.out.println("1.Actor\n2.Serial\n3.Film");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    switch(choice) {
                        case 1:
                            System.out.println("Introduceți numele actorului:");
                            String actorName = scanner.nextLine();

                            Actor searchActor = null;
                            for (Actor actor : actors) {
                                if (actor.getName().toLowerCase().contains(actorName.toLowerCase())) {
                                    searchActor = actor;
                                    break;
                                }
                            }

                            if (searchActor != null) {
                                System.out.println(searchActor);

                            } else {
                                System.out.println("Actorul nu a fost găsit.");
                            }
                            break;
                        case 2:
                            break;
                        case 3:
                            System.out.println("Introduceți titlul filmului:");
                            String movieTitle = scanner.nextLine();

                            Movie searchMovie = null;
                            for (Movie movie : movies) {
                                if (movie.getTitle().toLowerCase().contains(movieTitle.toLowerCase())) {
                                    searchMovie = movie;
                                    break;
                                }
                            }

                            if (searchMovie != null) {
                                searchMovie.displayInfo();
                            } else {
                                System.out.println("Filmul nu a fost găsit.");
                            }
                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;

                    }
                    break;
                case 5:
                    System.out.println("Alegeti optiunea:\n1.Adaugare\n2.Stergere");
                    int alegere = scanner.nextInt();
                    scanner.nextLine();
                    switch(alegere){
                        case 1:
                            System.out.println("Alegeti optiunea:\n1.Adaugare Actor\n2.Adaugare Productie");
                            int adaugare = scanner.nextInt();
                            scanner.nextLine();
                            switch(adaugare){
                                case 1:
                                    for (Actor actor : actors) {
                                        System.out.println(actor);
                                    }
                                    System.out.println("Introduceti numele actorului");
                                    String actorName = scanner.nextLine();
                                    Actor searchActor = null;

                                    for (Actor actor : actors) {
                                        if (actor.getName().equalsIgnoreCase(actorName)) {
                                            searchActor = actor;
                                            break;
                                        }
                                    }
                                    if(searchActor != null){
                                        contributor.favorites.add(searchActor);
                                        contributor.getFavorites();
                                    }else{
                                        System.out.println("Actorul nu a fost gasit.");
                                    }
                                    break;
                                case 2:
                                    System.out.println("Alegeti optiunea:\n1.Adaugare Film\n2.Adaugare Serial");
                                    int productie = scanner.nextInt();
                                    scanner.nextLine();
                                    switch(productie){
                                        case 1:
                                            for(Movie movie : movies){
                                                movie.displayInfo();
                                            }
                                            System.out.println("Introduceti numele filmului:");
                                            String filmName = scanner.nextLine();
                                            Movie existingMovie = null;

                                            for(Movie movie : movies){
                                                if(movie.getTitle().equalsIgnoreCase(filmName)){
                                                    existingMovie = movie;
                                                    existingMovie.displayInfo();
                                                    break;
                                                }

                                            }

                                            if(existingMovie != null){
                                                contributor.addFavoriteProduction(existingMovie);
                                                contributor.getProductionFavorites();
                                            }else{
                                                System.out.println("Filmul nu a fost gasit.");
                                            }
                                            break;
                                        case 2:
                                            for (Series series : series) {
                                                series.displayInfo();
                                            }
                                            System.out.println("Introduceti numele serialului:");
                                            String seriesName = scanner.nextLine();
                                            Series existingSeries = null;

                                            for (Series series : series) {
                                                if (series.getTitle().equalsIgnoreCase(seriesName)) {
                                                    existingSeries = series;
                                                    break;
                                                }
                                            }

                                            if (existingSeries != null) {
                                                contributor.addFavoriteProduction(existingSeries);
                                                contributor.getProductionFavorites();
                                            } else {
                                                System.out.println("Serialul nu a fost gasit.");
                                            }

                                            break;
                                        default:
                                            System.out.println("Opțiune invalidă. Încercați din nou.");
                                            break;
                                    }
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        case 2:
                            System.out.println("Alegeti optiunea:\n1.Stergere Actor\n2.Stergere Productie");
                            int stergere = scanner.nextInt();
                            scanner.nextLine();
                            switch(stergere){
                                case 1:
                                    contributor.getFavorites();
                                    System.out.println("Introduceti numele actorului:");
                                    String actorName = scanner.nextLine();

                                    boolean actorRemoved = false;
                                    for (Object favorite : contributor.favorites) {
                                        if (favorite instanceof Actor && ((Actor) favorite).getName().equalsIgnoreCase(actorName)) {
                                            contributor.removeFavorite((Comparable<?>) favorite);
                                            actorRemoved = true;
                                            System.out.println("Actorul a fost sters cu succes!");
                                            break;
                                        }
                                    }

                                    if (!actorRemoved) {
                                        System.out.println("Actorul nu a fost gasit in lista de favorite.");
                                    }

                                    contributor.getFavorites();
                                    break;
                                case 2:
                                    contributor.getProductionFavorites();
                                    System.out.println("Introduceti numele productiei:");
                                    String productionName = scanner.nextLine();

                                    // Iterate through favorites to find and remove the production by name
                                    boolean productionRemoved = false;
                                    for (Object favorite : contributor.favorites) {
                                        if (favorite instanceof Production && ((Production) favorite).getTitle().equalsIgnoreCase(productionName)) {
                                            contributor.removeFavoriteProduction((Production) favorite);
                                            productionRemoved = true;
                                            System.out.println("Productia a fost stearsa cu succes!");
                                            break;
                                        }
                                    }

                                    if (!productionRemoved) {
                                        System.out.println("Productia nu a fost gasita in lista de favorite.");
                                    }

                                    contributor.getProductionFavorites();
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }

                    break;
                case 6:
                    System.out.println("Alegeti optiunea:\n1.Creare cerere\n2.Stergere cerere");
                    int cerere = scanner.nextInt();
                    scanner.nextLine();
                    switch(cerere){
                        case 1:
                            Request request = new Request();
                            contributor.createRequest(request);
                            requests.add(request);
                            break;
                        case 2:
                            int index = 1;
                            for (Request request1 : requests) {
                                if (request1.getCreatedByUsername().equals(contributor.getUsername())) {
                                    System.out.println("pula mea mare");
                                    System.out.println(index + ". " + request1);
                                    index++;
                                }
                            }

                            if(index != 1){
                                System.out.print("Introduceți numărul cererii pe care doriți să o ștergeți: ");
                                int alegere1 = scanner.nextInt();
                                scanner.nextLine();

                                if (alegere1 > 0 && alegere1 <= index) {
                                    Request cerereSelectata = null;
                                    index = 1;
                                    for (Request request1 : requests) {
                                        if (request1.getCreatedByUsername().equals(contributor.getUsername())) {
                                            if (index == alegere1) {
                                                cerereSelectata = request1;
                                                break;
                                            }
                                            index++;
                                        }
                                    }

                                    if (cerereSelectata != null) {
                                        contributor.deleteRequest(cerereSelectata);
                                        requests.remove(cerereSelectata);
                                    } else {
                                        System.out.println("Eroare la selectarea cererii.");
                                    }
                                } else {
                                    System.out.println("Indexul cererii este invalid.");
                                }
                            }else{
                                System.out.println("Nu aveti cereri pe care sa le stergeti.");
                            }

                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }
                    break;
                case 7:
                    System.out.println("Alegeti optiunea:\n1.Adaugare\n2.Stergere");
                    int userChoice = scanner.nextInt();
                    scanner.nextLine();
                    switch(userChoice){
                        case 1:
                            System.out.println("Alegeti optiunea:\n1.Adaugare Actor\n2.Adaugare Productie");
                            int addChoice = scanner.nextInt();
                            scanner.nextLine();
                            switch(addChoice){
                                case 1:
                                    Actor newActor = new Actor();
                                    contributor.addActorSystem(newActor);
                                    actors.add(newActor);
                                    contributor.experience = actorExperience.calculateExperience(contributor);
                                    System.out.println("Experienta updatata: " + contributor.experience);
                                    break;
                                case 2:
                                    System.out.println("Alegeti optiunea:\n1.Film\n2.Serial");
                                    int productie = scanner.nextInt();
                                    scanner.nextLine();
                                    switch(productie){
                                        case 1:
                                            String duration = null;
                                            int releaseYear = 0;
                                            Movie newMovie = new Movie();
                                            contributor.addProductionSystem(newMovie);
                                            while (true) {
                                                try {
                                                    System.out.println("Introduceti durata(Minute):");
                                                    duration = String.valueOf(Integer.parseInt(scanner.nextLine()));
                                                    newMovie.setDuration(String.valueOf(Integer.parseInt(duration)));
                                                    break;
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Durata trebuie sa fie un numar. Incercati din nou.");
                                                }
                                            }

                                            while (true) {
                                                try {
                                                    System.out.println("Introduceti anul de lansare:");
                                                    releaseYear = Integer.parseInt(scanner.nextLine());
                                                    if (releaseYear > 2024) {
                                                        System.out.println("Anul de lansare nu poate fi mai mare de 2024. Incercati din nou.");
                                                        continue;
                                                    }
                                                    newMovie.setReleaseYear(releaseYear);
                                                    break;
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Anul introdus nu este un numar valid. Incercati din nou.");
                                                }
                                            }
                                            contributor.experience = productionExperience.calculateExperience(contributor);
                                            System.out.println(contributor.experience);
                                            movies.add(newMovie);
                                            contributor.userCreated.add(newMovie);
                                            break;
                                        case 2:
                                            int numberOfSeasons = 0;
                                            Series newSeries = new Series();
                                            contributor.addProductionSystem(newSeries);
                                            while (true) {
                                                try {
                                                    System.out.println("Introduceti anul de lansare:");
                                                    releaseYear = Integer.parseInt(scanner.nextLine());
                                                    if (releaseYear > 2024) {
                                                        System.out.println("Anul de lansare nu poate fi mai mare de 2024. Incercati din nou.");
                                                        continue;
                                                    }
                                                    newSeries.setReleaseYear(releaseYear);
                                                    break;
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Anul introdus nu este un numar valid. Incercati din nou.");
                                                }
                                            }

                                            while (true) {
                                                try {
                                                    System.out.println("Introduceti numarul de sezoane:");
                                                    numberOfSeasons = Integer.parseInt(scanner.nextLine());
                                                    newSeries.setNumberOfSeasons(numberOfSeasons);
                                                    break;
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Numarul de sezoane trebuie sa fie un numar. Incercati din nou.");
                                                }
                                            }

                                            Map<String, List<Episode>> episodesBySeason = new TreeMap<>();
                                            for (int seasonNumber = 1; seasonNumber <= numberOfSeasons; seasonNumber++) {
                                                System.out.println("Introduceti detaliile pentru sezonul " + seasonNumber + ":");

                                                List<Episode> episodesForSeason = new LinkedList<>();
                                                for (int episodeNumber = 1; ; episodeNumber++) {
                                                    System.out.println("Introduceti numele episodului (introduceti 'done' cand ati terminat):");
                                                    String episodeName = scanner.nextLine();

                                                    if (episodeName.equalsIgnoreCase("done")) {
                                                        break;
                                                    }

                                                    while (true) {
                                                        try {
                                                            System.out.println("Introduceti durata episodului (minute):");
                                                            duration = String.valueOf(Integer.parseInt(scanner.nextLine()));
                                                            Episode episode = new Episode(episodeName, duration);
                                                            episodesForSeason.add(episode);
                                                            break;
                                                        } catch (NumberFormatException e) {
                                                            System.out.println("Durata episodului trebuie sa fie un numar. Incercati din nou.");
                                                        }
                                                    }
                                                }

                                                episodesBySeason.put("Season " + seasonNumber, episodesForSeason);
                                            }

                                            newSeries.setEpisodesBySeason(episodesBySeason);

                                            series.add(newSeries);
                                            contributor.experience = productionExperience.calculateExperience(contributor);
                                            System.out.println(contributor.experience);
                                            contributor.userCreated.add(newSeries);
                                            System.out.println("Serialul a fost adaugat cu succes in sistem!");
                                            break;
                                        default:
                                            System.out.println("Opțiune invalidă. Încercați din nou.");
                                            break;
                                    }
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        case 2:
                            System.out.println("Alegeti optiunea:\n1.Stergere Productie\n2.Stergere Actor");
                            int removeChoice = scanner.nextInt();
                            scanner.nextLine();
                            switch(removeChoice){
                                case 1:
                                    for(Movie movie : movies){
                                        movie.displayInfo();
                                    }
                                    for(Series serie : series){
                                        serie.displayInfo();
                                    }
                                    System.out.println("Introduceti numele productiei");
                                    String productionName = scanner.nextLine();
                                    contributor.removeProductionSystem(productionName);
                                    break;
                                case 2:
                                    for (Actor actor : actors) {
                                        System.out.println(actor);
                                    }
                                    System.out.println("Introduceti numele actorului");
                                    String actorName = scanner.nextLine();
                                    contributor.removeActorSystem(actorName);
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }
                    break;
                case 8:
                    System.out.println("Alegeti optiunea:\n1.Delogare\n2.Inchidere program");
                    int logoutChoice = scanner.nextInt();
                    scanner.nextLine();
                    switch(logoutChoice){
                        case 1:
                            loggedIn = false;
                            break;
                        case 2:
                            System.out.println("Programul se închide. La revedere!");
                            System.exit(0);
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }
                    break;
                case 9:
                    System.out.println("Alege optiunea de actualizare:\n1.Productie\n2.Actor");
                    int updateChoice = scanner.nextInt();
                    scanner.nextLine();

                    switch (updateChoice) {
                        case 1:
                            System.out.println("Alege optiunea:\n1.Film\n2.Serial");
                            int productie = scanner.nextInt();
                            scanner.nextLine();
                            switch (productie){
                                case 1:
                                    boolean updateAnotherField = true;
                                    System.out.println("Introduceti numele filmului pe care doriti sa-l actualizati:");
                                    String filmName = scanner.nextLine();
                                    Movie searchMovie = null;
                                    for(Movie movie : movies){
                                        if(movie.getTitle().toLowerCase().contains(filmName.toLowerCase())){
                                            searchMovie = movie;
                                            break;
                                        }
                                    }
                                    if(searchMovie == null){
                                        System.out.println("Filmul nu a fost gasit.");
                                        break;
                                    }else if(contributor.userCreated.contains(searchMovie)){
                                        System.out.println("Informatiile curente pentru filmul " + searchMovie.getTitle() + ":");
                                        searchMovie.displayInfo();
                                        contributor.updateProduction(searchMovie);
                                    }else{
                                        System.out.println("Nu aveti voie sa schimbati aceasta productie.");
                                        break;
                                    }

                                    System.out.print("Doriti sa actualizati si durata/anul de lansare? (Y/N): ");
                                    String updateAnother = scanner.nextLine().trim().toUpperCase();
                                    updateAnotherField = updateAnother.equals("Y");

                                    while(updateAnotherField){
                                        System.out.println("6. Durata");
                                        System.out.println("7. Anul de lansare");
                                        int decizie = scanner.nextInt();
                                        scanner.nextLine();
                                        switch(decizie){
                                            case 6:
                                                while (true) {
                                                    try {
                                                        System.out.println("Introduceti noua durata (Minute):");
                                                        int newDuration = Integer.parseInt(scanner.nextLine());
                                                        searchMovie.setDuration(String.valueOf(newDuration));
                                                        break;
                                                    } catch (NumberFormatException e) {
                                                        System.out.println("Durata trebuie sa fie un numar. Incercati din nou.");
                                                    }
                                                }
                                                break;
                                            case 7:
                                                while (true) {
                                                    try {
                                                        System.out.println("Introduceti noul an de lansare:");
                                                        int newReleaseYear = Integer.parseInt(scanner.nextLine());
                                                        if (newReleaseYear > 2024) {
                                                            System.out.println("Anul de lansare nu poate fi mai mare de 2024. Incercati din nou.");
                                                            continue;
                                                        }
                                                        searchMovie.setReleaseYear(newReleaseYear);
                                                        break;
                                                    } catch (NumberFormatException e) {
                                                        System.out.println("Anul introdus nu este un numar valid. Incercati din nou.");
                                                    }
                                                }
                                                break;
                                        }
                                        System.out.print("Doriti sa actualizati altceva? (Y/N): ");
                                        updateAnother = scanner.nextLine().trim().toUpperCase();
                                        updateAnotherField = updateAnother.equals("Y");
                                    }
                                    System.out.println("Informatiile pentru filmul " + searchMovie.getTitle() + " au fost actualizate cu succes.");
                                    break;
                                case 2:
                                    System.out.println("Introduceti numele serialului pe care doriti sa-l actualizati:");
                                    String seriesName = scanner.nextLine();

                                    Series serie = null;
                                    for (Series series : series) {
                                        if (series.getTitle().equalsIgnoreCase(seriesName)) {
                                            serie = series;
                                            break;
                                        }
                                    }

                                    if (serie == null) {
                                        System.out.println("Serialul nu a fost gasit");
                                        break;
                                    }else if(contributor.userCreated.contains(serie)){
                                        System.out.println("Informatiile curente pentru serialul " + seriesName + ":");
                                        serie.displayInfo();
                                        contributor.updateProduction(serie);
                                    }else{
                                        System.out.println("Mama mea e florareasa");
                                        break;
                                    }

                                    updateAnotherField = true;

                                    System.out.print("Doriti sa actualizati si anul de lansare/numarul de sezoane/episoade? (Y/N): ");
                                    updateAnother = scanner.nextLine().trim().toUpperCase();
                                    updateAnotherField = updateAnother.equals("Y");

                                    while (updateAnotherField) {

                                        System.out.println("6. Anul de lansare");
                                        System.out.println("7. Numarul de sezoane");
                                        System.out.println("8. Episoade (individual)");
                                        int seriesChoice = scanner.nextInt();
                                        scanner.nextLine();
                                        switch(seriesChoice){
                                            case 6:
                                                while (true) {
                                                    try {
                                                        System.out.println("Introduceti noul an de lansare:");
                                                        int newReleaseYear = Integer.parseInt(scanner.nextLine());
                                                        if (newReleaseYear > 2024) {
                                                            System.out.println("Anul de lansare nu poate fi mai mare de 2024. Incercati din nou.");
                                                            continue;
                                                        }
                                                        serie.setReleaseYear(newReleaseYear);
                                                        break;
                                                    } catch (NumberFormatException e) {
                                                        System.out.println("Anul introdus nu este un numar valid. Incercati din nou.");
                                                    }
                                                }
                                                break;
                                            case 7:
                                                while (true) {
                                                    try {
                                                        System.out.println("Introduceti noul numar de sezoane:");
                                                        int newNumberOfSeasons = Integer.parseInt(scanner.nextLine());
                                                        serie.setNumberOfSeasons(newNumberOfSeasons);
                                                        break;
                                                    } catch (NumberFormatException e) {
                                                        System.out.println("Numarul de sezoane trebuie sa fie un numar. Incercati din nou.");
                                                    }
                                                }
                                                break;
                                            case 8:
                                                Map<String, List<Episode>> updatedEpisodesBySeason = new TreeMap<>();
                                                for (int seasonNumber = 1; seasonNumber <= serie.getNumberOfSeasons(); seasonNumber++) {
                                                    System.out.println("Introduceti detaliile pentru sezonul " + seasonNumber + ":");

                                                    List<Episode> updatedEpisodesForSeason = new LinkedList<>();
                                                    for (int episodeNumber = 1; ; episodeNumber++) {
                                                        System.out.println("Introduceti numele episodului (introduceti 'done' cand ati terminat):");
                                                        String episodeName = scanner.nextLine();

                                                        if (episodeName.equalsIgnoreCase("done")) {
                                                            break;
                                                        }

                                                        while (true) {
                                                            try {
                                                                System.out.println("Introduceti durata episodului (minute):");
                                                                String duration = String.valueOf(Integer.parseInt(scanner.nextLine()));
                                                                Episode episode = new Episode(episodeName, duration);
                                                                updatedEpisodesForSeason.add(episode);
                                                                break;
                                                            } catch (NumberFormatException e) {
                                                                System.out.println("Durata episodului trebuie sa fie un numar. Incercati din nou.");
                                                            }
                                                        }
                                                    }

                                                    updatedEpisodesBySeason.put("Season " + seasonNumber, updatedEpisodesForSeason);
                                                }

                                                serie.setEpisodesBySeason(updatedEpisodesBySeason);
                                                System.out.println("Episoadele pentru serialul " + serie.getTitle() + " au fost actualizate cu succes.");
                                                break;
                                            default:
                                                System.out.println("Optiune invalida.");
                                                break;

                                        }
                                        System.out.print("Doriti sa actualizati si alte campuri? (Y/N): ");
                                        updateAnother = scanner.nextLine().trim().toUpperCase();
                                        updateAnotherField = updateAnother.equals("Y");
                                    }
                                    System.out.println("Informatiile pentru serialul " + seriesName + " au fost actualizate cu succes.");
                                    break;
                                default:
                                    System.out.println("Optiune invalida. Incercati din nou.");
                                    break;

                            }

                            break;

                        case 2:
                            System.out.println("Introduceti numele actorului:");
                            String actorName = scanner.nextLine();
                            Actor searchActor = null;

                            for (Actor actor : actors) {
                                if (actor.getName().equalsIgnoreCase(actorName)) {
                                    searchActor = actor;
                                    break;
                                }
                            }

                            if (searchActor != null && contributor.userCreated.contains(searchActor)) {
                                System.out.println("Actorul a fost gasit!\nAlegeti optiunea:\n1.Modifica Nume\n2.Modifica Biografie\n3.Modifica Performante");
                                int changeInfo = scanner.nextInt();
                                scanner.nextLine();

                                switch (changeInfo) {
                                    case 1:
                                        System.out.println("Introduceti noul nume:");
                                        String newName = scanner.nextLine();
                                        searchActor.setName(newName);
                                        break;

                                    case 2:
                                        System.out.println("Introduceti modificarile de biografie:");
                                        String newBiography = scanner.nextLine();
                                        searchActor.setBiography(newBiography);
                                        break;

                                    case 3:
                                        System.out.println("Alegeti optiunea:\n1.Adaugare performanta\n2.Stergere performanta");
                                        int modifyPerformanceChoice = scanner.nextInt();
                                        scanner.nextLine();

                                        switch (modifyPerformanceChoice) {
                                            case 1:
                                                System.out.println("Introduceti numele noii performante:");
                                                String newPerformanceName = scanner.nextLine();
                                                System.out.println("Introduceti tipul noii performante:");
                                                String newPerformanceType = scanner.nextLine();

                                                Actor.NameTypePair newPerformance = new Actor.NameTypePair(newPerformanceName, newPerformanceType);
                                                searchActor.getRoles().add(newPerformance);
                                                System.out.println("Performanta a fost adaugata cu succes!");
                                                break;

                                            case 2:
                                                System.out.println("Introduceti numele performantei de sters:");
                                                String performanceToDelete = scanner.nextLine();

                                                Iterator<Actor.NameTypePair> iterator = searchActor.getRoles().iterator();
                                                while (iterator.hasNext()) {
                                                    Actor.NameTypePair role = iterator.next();
                                                    if (role.getName().equalsIgnoreCase(performanceToDelete)) {
                                                        iterator.remove();
                                                        System.out.println("Performanta a fost stearsa cu succes!");
                                                        break;
                                                    }
                                                }
                                                break;

                                            default:
                                                System.out.println("Opțiune invalidă. Încercați din nou.");
                                                break;
                                        }
                                        break;

                                    default:
                                        System.out.println("Optiune invalida. Incercati din nou.");
                                        break;
                                }
                            } else {
                                System.out.println("Actorul nu a fost găsit / Nu aveti permisiune de modificare");
                            }
                            break;

                        default:
                            System.out.println("Optiune invalida. Incercati din nou.");
                            break;
                    }
                    break;
                case 10:
                    List<Request> assignedRequests = contributor.getAssignedRequests();
                    System.out.println("Cereri asignate:");
                    for (int i = 0; i < assignedRequests.size(); i++) {
                        System.out.println((i + 1) + ". " + assignedRequests.get(i));
                    }

                    System.out.println("Selectati cererea de procesat: (Introduceti numarul corespunzator):");
                    int requestIndex = scanner.nextInt();
                    scanner.nextLine();

                    if (requestIndex >= 1 && requestIndex <= assignedRequests.size()) {
                        Request selectedRequest = assignedRequests.get(requestIndex - 1);

                        System.out.println("Doriti sa acceptati sau sa refuzati cererea? (Introdu 'accept' sau 'deny'):");
                        String decision = scanner.nextLine().toLowerCase();

                        if ("accept".equals(decision) || "deny".equals(decision)) {
                            if ("accept".equals(decision)) {
                                selectedRequest.notifyObservers("Cererea ta "+selectedRequest+" a fost acceptata si va fi rezolvata curand!", 1);
                                for(Regular regular : regularUsers){
                                    if (selectedRequest.getCreatedByUsername().equals(regular.getUsername())){
                                        selectedRequest.removeObserver(regular);
                                        break;
                                    }
                                }
                                for(Contributor contributor1 : contributors){
                                    if(selectedRequest.getCreatedByUsername().equals(contributor1.getUsername())){
                                        selectedRequest.removeObserver(contributor1);
                                        break;
                                    }
                                }
                                contributor.assignedRequests.remove(selectedRequest);
                                requests.remove(selectedRequest);
                                System.out.println("Cerere acceptata!");
                                if(selectedRequest.getRequestType().equals(Request.RequestType.MOVIE_ISSUE) || selectedRequest.getRequestType().equals(Request.RequestType.ACTOR_ISSUE)){
                                    for(Regular regular : regularUsers){
                                        if(selectedRequest.getCreatedByUsername().equals(regular.username)){
                                            regular.experience = issueExperience.calculateExperience(regular);
                                            System.out.println(regular.experience);
                                            break;
                                        }
                                    }
                                    for(Contributor contributor1 : contributors){
                                        if(selectedRequest.getCreatedByUsername().equals(contributor1.username)){
                                            contributor1.experience = issueExperience.calculateExperience(contributor1);
                                            System.out.println(contributor1.experience);
                                        }
                                    }
                                }

                            } else if ("deny".equals(decision)) {
                                selectedRequest.notifyObservers("Cererea ta "+selectedRequest+" a fost refuzata.", 1);
                                for(Regular regular : regularUsers){
                                    if (selectedRequest.getCreatedByUsername().equals(regular.getUsername())){
                                        selectedRequest.removeObserver(regular);
                                        break;
                                    }
                                }
                                for(Contributor contributor1 : contributors){
                                    if(selectedRequest.getCreatedByUsername().equals(contributor1.getUsername())){
                                        selectedRequest.removeObserver(contributor1);
                                        break;
                                    }
                                }
                                contributor.assignedRequests.remove(selectedRequest);
                                requests.remove(selectedRequest);
                                System.out.println("Cerere refuzata.");
                            }
                        } else {
                            System.out.println("Decizie invalida. Va rugam introduceti 'accept' sau 'deny'.");
                        }
                    } else {
                        System.out.println("Index cerere invalid.");
                    }
                    break;
                default:
                    System.out.println("Opțiune invalidă. Încercați din nou.");
                    break;
            }
        }
    }

    private void handleAdminFlow(Admin admin, Scanner scanner) {
        boolean loggedIn = true;
        System.out.println("Bine ați venit, " + admin.getUsername() + "!");


        while (loggedIn) {
            displayOptionsAdmin();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Alegeți opțiunea dorită:");
                    System.out.println("1. Afișează toate producțiile");
                    System.out.println("2. Filtrează producțiile după gen");

                    int option = scanner.nextInt();
                    scanner.nextLine();

                    switch (option) {
                        case 1:
                            System.out.println("Detalii despre toate producțiile din sistem:");

                            System.out.println("Movies:");
                            for (Movie movie : movies) {
                                movie.displayInfo();
                            }

                            System.out.println("Series:");
                            for (Series series : series) {
                                series.displayInfo();
                            }
                            break;

                        case 2:
                            System.out.println("Introduceti un gen pentru a filtra rezultatele (introduceti 'done' cand ati terminat):");
                            String filterGenreStr;
                            while (!(filterGenreStr = scanner.nextLine()).equalsIgnoreCase("done")) {
                                try {
                                    Genre filterGenre = Genre.valueOf(filterGenreStr);

                                    System.out.println("Movies with genre " + filterGenre + ":");
                                    for (Movie movie : movies) {
                                        if (movie.getGenres().contains(filterGenre)) {
                                            movie.displayInfo();
                                        }
                                    }

                                    System.out.println("Series with genre " + filterGenre + ":");
                                    for (Series seriesItem : series) {
                                        if (seriesItem.getGenres().contains(filterGenre)) {
                                            seriesItem.displayInfo();
                                        }
                                    }

                                } catch (IllegalArgumentException e) {
                                    System.out.println("Genul introdus nu este valid. Incercati din nou.");
                                }
                            }
                            break;

                        default:
                            System.out.println("Opțiunea introdusă nu este validă. Adaugarea a fost anulată.");
                            break;
                    }
                    break;
                case 2:
                    System.out.println("Doriti sa sortati dupa nume?\n1.Da\n2.Nu");
                    int sort = scanner.nextInt();
                    scanner.nextLine();
                    switch(sort){
                        case 1:
                            List<Actor> sortedActors = new ArrayList<>(actors);
                            sortedActors.sort(Comparator.comparing(Actor::getName));

                            for (Actor actor : sortedActors) {
                                System.out.println(actor);
                            }
                            break;
                        case 2:
                            for (Actor actor : actors) {
                                System.out.println(actor);
                            }
                            break;
                        default:
                            System.out.println("Optiune invalida");
                            break;
                    }
                    break;
                case 3:
                    if(admin.getNotifications().isEmpty()){
                        System.out.println("\nNu aveti notificari.\n");
                    }else{
                        System.out.println("");
                        admin.getNotifications().forEach(System.out::println);
                        System.out.println("");
                    }
                    break;
                case 4:
                    System.out.println("1.Actor\n2.Serial\n3.Film");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    switch(choice) {
                        case 1:
                            System.out.println("Introduceți numele actorului:");
                            String actorName = scanner.nextLine();

                            Actor searchActor = null;
                            for (Actor actor : actors) {
                                if (actor.getName().toLowerCase().contains(actorName.toLowerCase())) {
                                    searchActor = actor;
                                    break;
                                }
                            }

                            if (searchActor != null) {
                                System.out.println(searchActor);

                            } else {
                                System.out.println("Actorul nu a fost găsit.");
                            }
                            break;
                        case 2:
                            System.out.println("Introduceti numele serialului:");
                            String seriesName = scanner.nextLine();

                            Series searchSeries = null;
                            for (Series series : series){
                                if (series.getTitle().toLowerCase().contains(seriesName.toLowerCase())){
                                    searchSeries = series;
                                    break;
                                }
                            }
                            if (searchSeries != null){
                                searchSeries.displayInfo();
                            }else{
                                System.out.println("Serialul nu a fost gasit.");
                            }
                            break;
                        case 3:
                            System.out.println("Introduceți titlul filmului:");
                            String movieTitle = scanner.nextLine();

                            Movie searchMovie = null;
                            for (Movie movie : movies) {
                                if (movie.getTitle().toLowerCase().contains(movieTitle.toLowerCase())) {
                                    searchMovie = movie;
                                    break;
                                }
                            }

                            if (searchMovie != null) {
                                searchMovie.displayInfo();
                            } else {
                                System.out.println("Filmul nu a fost găsit.");
                            }
                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;

                    }
                    break;
                case 5:
                    System.out.println("Alegeti optiunea:\n1.Adaugare\n2.Stergere");
                    int alegere = scanner.nextInt();
                    scanner.nextLine();
                    switch(alegere){
                        case 1:
                            System.out.println("Alegeti optiunea:\n1.Adaugare Actor\n2.Adaugare Productie");
                            int adaugare = scanner.nextInt();
                            scanner.nextLine();
                            switch(adaugare){
                                case 1:
                                    for (Actor actor : actors) {
                                        System.out.println(actor);
                                    }
                                    System.out.println("Introduceti numele actorului");
                                    String actorName = scanner.nextLine();
                                    Actor searchActor = null;

                                    for (Actor actor : actors) {
                                        if (actor.getName().equalsIgnoreCase(actorName)) {
                                            searchActor = actor;
                                            break;
                                        }
                                    }
                                    if(searchActor != null){
                                        admin.addFavorite(searchActor);
                                        admin.getFavorites();
                                    }else{
                                        System.out.println("Actorul nu a fost gasit.");
                                    }
                                    break;
                                case 2:
                                    System.out.println("Alegeti optiunea:\n1.Adaugare Film\n2.Adaugare Serial");
                                    int productie = scanner.nextInt();
                                    scanner.nextLine();
                                    switch(productie){
                                        case 1:
                                            for(Movie movie : movies){
                                                movie.displayInfo();
                                            }
                                            System.out.println("Introduceti numele filmului:");
                                            String filmName = scanner.nextLine();
                                            Movie existingMovie = null;

                                            for(Movie movie : movies){
                                                if(movie.getTitle().equalsIgnoreCase(filmName)){
                                                    existingMovie = movie;
                                                    break;
                                                }

                                            }

                                            if(existingMovie != null){
                                                admin.addFavoriteProduction(existingMovie);
                                                admin.getProductionFavorites();
                                            }else{
                                                System.out.println("Filmul nu a fost gasit.");
                                            }
                                            break;
                                        case 2:
                                            for (Series series : series) {
                                                series.displayInfo();
                                            }
                                            System.out.println("Introduceti numele serialului:");
                                            String seriesName = scanner.nextLine();
                                            Series existingSeries = null;

                                            for (Series series : series) {
                                                if (series.getTitle().equalsIgnoreCase(seriesName)) {
                                                    existingSeries = series;
                                                    break;
                                                }
                                            }

                                            if (existingSeries != null) {
                                                admin.addFavoriteProduction(existingSeries);
                                                admin.getProductionFavorites();
                                            } else {
                                                System.out.println("Serialul nu a fost gasit.");
                                            }

                                            break;
                                        default:
                                            System.out.println("Opțiune invalidă. Încercați din nou.");
                                            break;
                                    }
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        case 2:
                            System.out.println("Alegeti optiunea:\n1.Stergere Actor\n2.Stergere Productie");
                            int stergere = scanner.nextInt();
                            scanner.nextLine();
                            switch(stergere){
                                case 1:
                                    admin.getFavorites();
                                    System.out.println("Introduceti numele actorului:");
                                    String actorName = scanner.nextLine();

                                    boolean actorRemoved = false;
                                    for (Object favorite : admin.favorites) {
                                        if (favorite instanceof Actor && ((Actor) favorite).getName().equalsIgnoreCase(actorName)) {
                                            admin.removeFavorite((Comparable<?>) favorite);
                                            actorRemoved = true;
                                            System.out.println("Actorul a fost sters cu succes!");
                                            break;
                                        }
                                    }

                                    if (!actorRemoved) {
                                        System.out.println("Actorul nu a fost gasit in lista de favorite.");
                                    }

                                    admin.getFavorites();
                                    break;
                                case 2:
                                    admin.getProductionFavorites();
                                    System.out.println("Introduceti numele productiei:");
                                    String productionName = scanner.nextLine();

                                    boolean productionRemoved = false;
                                    for (Object favorite : admin.favorites) {
                                        if (favorite instanceof Production && ((Production) favorite).getTitle().equalsIgnoreCase(productionName)) {
                                            admin.removeFavoriteProduction((Production) favorite);
                                            productionRemoved = true;
                                            System.out.println("Productia a fost stearsa cu succes!");
                                            break;
                                        }
                                    }

                                    if (!productionRemoved) {
                                        System.out.println("Productia nu a fost gasita in lista de favorite.");
                                    }

                                    admin.getProductionFavorites();
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }

                    break;
                case 6:
                    System.out.println("Alegeti optiunea:\n1.Adaugare utilizator\n2.Stergere utilizator");
                    int user = scanner.nextInt();
                    scanner.nextLine();
                    switch(user){
                        case 1:
                            registerNewUser(scanner);
                            break;
                        case 2:
                            removeUser(scanner);
                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }
                    break;
                case 7:
                    System.out.println("Alegeti optiunea:\n1.Adaugare\n2.Stergere");
                    int userChoice = scanner.nextInt();
                    scanner.nextLine();
                    switch(userChoice){
                        case 1:
                            System.out.println("Alegeti optiunea:\n1.Adaugare Actor\n2.Adaugare Productie");
                            int addChoice = scanner.nextInt();
                            scanner.nextLine();
                            switch(addChoice){
                                case 1:
                                    Actor newActor = new Actor();
                                    admin.addActorSystem(newActor);
                                    actors.add(newActor);
                                    admin.experience = actorExperience.calculateExperience(admin);
                                    break;
                                case 2:
                                    System.out.println("Alegeti optiunea:\n1.Film\n2.Serial");
                                    int productie = scanner.nextInt();
                                    scanner.nextLine();
                                    switch(productie){
                                        case 1:
                                            String duration = null;
                                            int releaseYear = 0;
                                            Movie newMovie = new Movie();
                                            admin.addProductionSystem(newMovie);
                                            while (true) {
                                                try {
                                                    System.out.println("Introduceti durata(Minute):");
                                                    duration = String.valueOf(Integer.parseInt(scanner.nextLine()));
                                                    newMovie.setDuration(String.valueOf(Integer.parseInt(duration)));
                                                    break;
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Durata trebuie sa fie un numar. Incercati din nou.");
                                                }
                                            }

                                            while (true) {
                                                try {
                                                    System.out.println("Introduceti anul de lansare:");
                                                    releaseYear = Integer.parseInt(scanner.nextLine());
                                                    if (releaseYear > 2024) {
                                                        System.out.println("Anul de lansare nu poate fi mai mare de 2024. Incercati din nou.");
                                                        continue;
                                                    }
                                                    newMovie.setReleaseYear(releaseYear);
                                                    break;
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Anul introdus nu este un numar valid. Incercati din nou.");
                                                }
                                            }
                                            movies.add(newMovie);
                                            admin.userCreated.add(newMovie);


                                            break;
                                        case 2:
                                            int numberOfSeasons = 0;
                                            Series newSeries = new Series();
                                            admin.addProductionSystem(newSeries);
                                            while (true) {
                                                try {
                                                    System.out.println("Introduceti anul de lansare:");
                                                    releaseYear = Integer.parseInt(scanner.nextLine());
                                                    if (releaseYear > 2024) {
                                                        System.out.println("Anul de lansare nu poate fi mai mare de 2024. Incercati din nou.");
                                                        continue;
                                                    }
                                                    newSeries.setReleaseYear(releaseYear);
                                                    break;
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Anul introdus nu este un numar valid. Incercati din nou.");
                                                }
                                            }

                                            while (true) {
                                                try {
                                                    System.out.println("Introduceti numarul de sezoane:");
                                                    numberOfSeasons = Integer.parseInt(scanner.nextLine());
                                                    newSeries.setNumberOfSeasons(numberOfSeasons);
                                                    break;
                                                } catch (NumberFormatException e) {
                                                    System.out.println("Numarul de sezoane trebuie sa fie un numar. Incercati din nou.");
                                                }
                                            }

                                            Map<String, List<Episode>> episodesBySeason = new TreeMap<>();
                                            for (int seasonNumber = 1; seasonNumber <= numberOfSeasons; seasonNumber++) {
                                                System.out.println("Introduceti detaliile pentru sezonul " + seasonNumber + ":");

                                                List<Episode> episodesForSeason = new LinkedList<>();
                                                for (int episodeNumber = 1; ; episodeNumber++) {
                                                    System.out.println("Introduceti numele episodului (introduceti 'done' cand ati terminat):");
                                                    String episodeName = scanner.nextLine();

                                                    if (episodeName.equalsIgnoreCase("done")) {
                                                        break;
                                                    }

                                                    while (true) {
                                                        try {
                                                            System.out.println("Introduceti durata episodului (minute):");
                                                            duration = String.valueOf(Integer.parseInt(scanner.nextLine()));
                                                            Episode episode = new Episode(episodeName, duration);
                                                            episodesForSeason.add(episode);
                                                            break;
                                                        } catch (NumberFormatException e) {
                                                            System.out.println("Durata episodului trebuie sa fie un numar. Incercati din nou.");
                                                        }
                                                    }
                                                }

                                                episodesBySeason.put("Season " + seasonNumber, episodesForSeason);
                                            }

                                            newSeries.setEpisodesBySeason(episodesBySeason);

                                            series.add(newSeries);
                                            admin.userCreated.add(newSeries);
                                            System.out.println("Serialul a fost adaugat cu succes in sistem!");
                                            break;
                                        default:
                                            System.out.println("Opțiune invalidă. Încercați din nou.");
                                            break;
                                    }
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        case 2:
                            System.out.println("Alegeti optiunea:\n1.Stergere Productie\n2.Stergere Actor");
                            int removeChoice = scanner.nextInt();
                            scanner.nextLine();
                            switch(removeChoice){
                                case 1:
                                    for(Movie movie : movies){
                                        movie.displayInfo();
                                    }
                                    for(Series serie : series){
                                        serie.displayInfo();
                                    }
                                    System.out.println("Introduceti numele productiei");
                                    String productionName = scanner.nextLine();
                                    admin.removeProductionSystem(productionName);
                                    break;
                                case 2:
                                    for (Actor actor : actors) {
                                        System.out.println(actor);
                                    }
                                    System.out.println("Introduceti numele actorului");
                                    String actorName = scanner.nextLine();
                                    admin.removeActorSystem(actorName);
                                    break;
                                default:
                                    System.out.println("Opțiune invalidă. Încercați din nou.");
                                    break;
                            }
                            break;
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }
                    break;
                case 8:
                    System.out.println("Alegeti optiunea:\n1.Delogare\n2.Inchidere program");
                    int logoutChoice = scanner.nextInt();
                    scanner.nextLine();
                    switch(logoutChoice){
                        case 1:
                            loggedIn = false;
                            break;
                        case 2:
                            System.out.println("Programul se închide. La revedere!");
                            System.exit(0);
                        default:
                            System.out.println("Opțiune invalidă. Încercați din nou.");
                            break;
                    }
                    break;
                case 9:
                    System.out.println("Alege optiunea de actualizare:\n1.Productie\n2.Actor");
                    int updateChoice = scanner.nextInt();
                    scanner.nextLine();

                    switch (updateChoice) {
                        case 1:
                            System.out.println("Alege optiunea:\n1.Film\n2.Serial");
                            int productie = scanner.nextInt();
                            scanner.nextLine();
                            switch (productie){
                                case 1:
                                    boolean updateAnotherField = true;
                                    System.out.println("Introduceti numele filmului pe care doriti sa-l actualizati:");
                                    String filmName = scanner.nextLine();
                                    Movie searchMovie = null;
                                    for(Movie movie : movies){
                                        if(movie.getTitle().toLowerCase().contains(filmName.toLowerCase())){
                                            searchMovie = movie;
                                            break;
                                        }
                                    }
                                    if(searchMovie == null){
                                        System.out.println("Filmul nu a fost gasit.");
                                        break;
                                    }else if(admin.userCreated.contains(searchMovie)){
                                        System.out.println("Informatiile curente pentru filmul " + searchMovie.getTitle() + ":");
                                        searchMovie.displayInfo();
                                        admin.updateProduction(searchMovie);
                                    }else{
                                        System.out.println("Nu aveti voie sa schimbati aceasta productie.");
                                        break;
                                    }

                                    System.out.print("Doriti sa actualizati si durata/anul de lansare? (Y/N): ");
                                    String updateAnother = scanner.nextLine().trim().toUpperCase();
                                    updateAnotherField = updateAnother.equals("Y");

                                    while(updateAnotherField){
                                        System.out.println("6. Durata");
                                        System.out.println("7. Anul de lansare");
                                        int decizie = scanner.nextInt();
                                        scanner.nextLine();
                                        switch(decizie){
                                            case 6:
                                                while (true) {
                                                    try {
                                                        System.out.println("Introduceti noua durata (Minute):");
                                                        int newDuration = Integer.parseInt(scanner.nextLine());
                                                        searchMovie.setDuration(String.valueOf(newDuration));
                                                        break;
                                                    } catch (NumberFormatException e) {
                                                        System.out.println("Durata trebuie sa fie un numar. Incercati din nou.");
                                                    }
                                                }
                                                break;
                                            case 7:
                                                while (true) {
                                                    try {
                                                        System.out.println("Introduceti noul an de lansare:");
                                                        int newReleaseYear = Integer.parseInt(scanner.nextLine());
                                                        if (newReleaseYear > 2024) {
                                                            System.out.println("Anul de lansare nu poate fi mai mare de 2024. Incercati din nou.");
                                                            continue;
                                                        }
                                                        searchMovie.setReleaseYear(newReleaseYear);
                                                        break;
                                                    } catch (NumberFormatException e) {
                                                        System.out.println("Anul introdus nu este un numar valid. Incercati din nou.");
                                                    }
                                                }
                                                break;
                                        }
                                        System.out.print("Doriti sa actualizati altceva? (Y/N): ");
                                        updateAnother = scanner.nextLine().trim().toUpperCase();
                                        updateAnotherField = updateAnother.equals("Y");
                                    }
                                    System.out.println("Informatiile pentru filmul " + searchMovie.getTitle() + " au fost actualizate cu succes.");
                                    break;
                                case 2:
                                    System.out.println("Introduceti numele serialului pe care doriti sa-l actualizati:");
                                    String seriesName = scanner.nextLine();

                                    Series serie = null;
                                    for (Series series : series) {
                                        if (series.getTitle().equalsIgnoreCase(seriesName)) {
                                            serie = series;
                                            break;
                                        }
                                    }

                                    if (serie == null) {
                                        System.out.println("Serialul nu a fost gasit");
                                        break;
                                    }else if(admin.userCreated.contains(serie)){
                                        System.out.println("Informatiile curente pentru serialul " + seriesName + ":");
                                        serie.displayInfo();
                                        admin.updateProduction(serie);
                                    }else{
                                        System.out.println("Mama mea e florareasa");
                                        break;
                                    }

                                    updateAnotherField = true;

                                    System.out.print("Doriti sa actualizati si anul de lansare/numarul de sezoane/episoade? (Y/N): ");
                                    updateAnother = scanner.nextLine().trim().toUpperCase();
                                    updateAnotherField = updateAnother.equals("Y");

                                    while (updateAnotherField) {

                                        System.out.println("6. Anul de lansare");
                                        System.out.println("7. Numarul de sezoane");
                                        System.out.println("8. Episoade (individual)");
                                        int seriesChoice = scanner.nextInt();
                                        scanner.nextLine();
                                        switch(seriesChoice){
                                            case 6:
                                                while (true) {
                                                    try {
                                                        System.out.println("Introduceti noul an de lansare:");
                                                        int newReleaseYear = Integer.parseInt(scanner.nextLine());
                                                        if (newReleaseYear > 2024) {
                                                            System.out.println("Anul de lansare nu poate fi mai mare de 2024. Incercati din nou.");
                                                            continue;
                                                        }
                                                        serie.setReleaseYear(newReleaseYear);
                                                        break;
                                                    } catch (NumberFormatException e) {
                                                        System.out.println("Anul introdus nu este un numar valid. Incercati din nou.");
                                                    }
                                                }
                                                break;
                                            case 7:
                                                while (true) {
                                                    try {
                                                        System.out.println("Introduceti noul numar de sezoane:");
                                                        int newNumberOfSeasons = Integer.parseInt(scanner.nextLine());
                                                        serie.setNumberOfSeasons(newNumberOfSeasons);
                                                        break;
                                                    } catch (NumberFormatException e) {
                                                        System.out.println("Numarul de sezoane trebuie sa fie un numar. Incercati din nou.");
                                                    }
                                                }
                                                break;
                                            case 8:
                                                Map<String, List<Episode>> updatedEpisodesBySeason = new TreeMap<>();
                                                for (int seasonNumber = 1; seasonNumber <= serie.getNumberOfSeasons(); seasonNumber++) {
                                                    System.out.println("Introduceti detaliile pentru sezonul " + seasonNumber + ":");

                                                    List<Episode> updatedEpisodesForSeason = new LinkedList<>();
                                                    for (int episodeNumber = 1; ; episodeNumber++) {
                                                        System.out.println("Introduceti numele episodului (introduceti 'done' cand ati terminat):");
                                                        String episodeName = scanner.nextLine();

                                                        if (episodeName.equalsIgnoreCase("done")) {
                                                            break;
                                                        }

                                                        while (true) {
                                                            try {
                                                                System.out.println("Introduceti durata episodului (minute):");
                                                                String duration = String.valueOf(Integer.parseInt(scanner.nextLine()));
                                                                Episode episode = new Episode(episodeName, duration);
                                                                updatedEpisodesForSeason.add(episode);
                                                                break;
                                                            } catch (NumberFormatException e) {
                                                                System.out.println("Durata episodului trebuie sa fie un numar. Incercati din nou.");
                                                            }
                                                        }
                                                    }

                                                    updatedEpisodesBySeason.put("Season " + seasonNumber, updatedEpisodesForSeason);
                                                }

                                                serie.setEpisodesBySeason(updatedEpisodesBySeason);
                                                System.out.println("Episoadele pentru serialul " + serie.getTitle() + " au fost actualizate cu succes.");
                                                break;
                                            default:
                                                System.out.println("Optiune invalida.");
                                                break;

                                        }
                                        System.out.print("Doriti sa actualizati si alte campuri? (Y/N): ");
                                        updateAnother = scanner.nextLine().trim().toUpperCase();
                                        updateAnotherField = updateAnother.equals("Y");
                                    }
                                    System.out.println("Informatiile pentru serialul " + seriesName + " au fost actualizate cu succes.");
                                    break;
                                default:
                                    System.out.println("Optiune invalida. Incercati din nou.");
                                    break;

                            }

                            break;

                        case 2:
                            System.out.println("Introduceti numele actorului:");
                            String actorName = scanner.nextLine();
                            Actor searchActor = null;

                            for (Actor actor : actors) {
                                if (actor.getName().equalsIgnoreCase(actorName)) {
                                    searchActor = actor;
                                    break;
                                }
                            }

                            if (searchActor != null && admin.userCreated.contains(searchActor)) {
                                System.out.println("Informatii despre actor: " + searchActor);
                                System.out.println("Actorul a fost gasit!\nAlegeti optiunea:\n1.Modifica Nume\n2.Modifica Biografie\n3.Modifica Performante");
                                int changeInfo = scanner.nextInt();
                                scanner.nextLine();

                                switch (changeInfo) {
                                    case 1:
                                        System.out.println("Introduceti noul nume:");
                                        String newName = scanner.nextLine();
                                        searchActor.setName(newName);
                                        break;

                                    case 2:
                                        System.out.println("Introduceti modificarile de biografie:");
                                        String newBiography = scanner.nextLine();
                                        searchActor.setBiography(newBiography);
                                        break;

                                    case 3:
                                        System.out.println("Alegeti optiunea:\n1.Adaugare performanta\n2.Stergere performanta\n");
                                        int modifyPerformanceChoice = scanner.nextInt();
                                        scanner.nextLine();

                                        switch (modifyPerformanceChoice) {
                                            case 1:
                                                System.out.println("Introduceti numele noii performante:");
                                                String newPerformanceName = scanner.nextLine();
                                                System.out.println("Introduceti tipul noii performante:");
                                                String newPerformanceType = scanner.nextLine();

                                                Actor.NameTypePair newPerformance = new Actor.NameTypePair(newPerformanceName, newPerformanceType);
                                                searchActor.getRoles().add(newPerformance);
                                                System.out.println("Performanta a fost adaugata cu succes!");
                                                break;

                                            case 2:
                                                System.out.println("Introduceti numele performantei de sters:");
                                                String performanceToDelete = scanner.nextLine();

                                                Iterator<Actor.NameTypePair> iterator = searchActor.getRoles().iterator();
                                                while (iterator.hasNext()) {
                                                    Actor.NameTypePair role = iterator.next();
                                                    if (role.getName().equalsIgnoreCase(performanceToDelete)) {
                                                        iterator.remove();
                                                        System.out.println("Performanta a fost stearsa cu succes!");
                                                        break;
                                                    }
                                                }
                                                break;

                                            default:
                                                System.out.println("Opțiune invalidă. Încercați din nou.");
                                                break;
                                        }
                                        break;

                                    default:
                                        System.out.println("Optiune invalida. Incercati din nou.");
                                        break;
                                }
                            } else {
                                System.out.println("Actorul nu a fost găsit / Nu aveti permisiune de modificare");
                            }
                            break;

                        default:
                            System.out.println("Optiune invalida. Incercati din nou.");
                            break;
                    }
                    break;
                case 10:
                    List<Request> assignedAdminRequests = admin.getAssignedRequests();
                    int i;
                    for (i = 0; i < assignedAdminRequests.size(); i++) {
                        System.out.println((i + 1) + ". " + assignedAdminRequests.get(i));
                    }

                    List<Request> globalAdminRequests = Admin.RequestHolder.getRequests();
                    for (int j = 0; j < globalAdminRequests.size(); j++) {
                        System.out.println((j + i + 1) + ". " + globalAdminRequests.get(j));
                    }

                    System.out.println("Selecteaza cererea pe care sa o procesezi: (Introdu numarul corespunzator):");
                    int requestIndex = scanner.nextInt();
                    scanner.nextLine();

                    if (requestIndex >= 1 && requestIndex <= assignedAdminRequests.size()) {
                        Request selectedRequest = assignedAdminRequests.get(requestIndex - 1);
                        System.out.println("Doriti sa acceptati sau sa refuzati cererea? (Introduceti 'accept' sau 'deny'):");
                        String decision = scanner.nextLine().toLowerCase();

                        if ("accept".equals(decision) || "deny".equals(decision)) {
                            if ("accept".equals(decision)) {
                                selectedRequest.notifyObservers("Cererea ta " + selectedRequest +" a fost acceptata si va fi rezolvata curand!", 1);
                                for(Regular regular : regularUsers){
                                    if (selectedRequest.getCreatedByUsername().equals(regular.getUsername())){
                                        selectedRequest.removeObserver(regular);
                                        break;
                                    }
                                }
                                for(Contributor contributor : contributors){
                                    if(selectedRequest.getCreatedByUsername().equals(contributor.getUsername())){
                                        selectedRequest.removeObserver(contributor);
                                        break;
                                    }
                                }
                                admin.assignedRequests.remove(selectedRequest);
                                Admin.RequestHolder.removeRequest(selectedRequest);
                                requests.remove(selectedRequest);
                                System.out.println("Cerere acceptata!");
                                if(selectedRequest.getRequestType().equals(Request.RequestType.MOVIE_ISSUE) || selectedRequest.getRequestType().equals(Request.RequestType.ACTOR_ISSUE)){
                                    for(Regular regular : regularUsers){
                                        if(selectedRequest.getCreatedByUsername().equals(regular.username)){
                                            regular.experience = issueExperience.calculateExperience(regular);
                                            System.out.println(regular.experience);
                                            break;
                                        }
                                    }
                                    for(Contributor contributor : contributors){
                                        if(selectedRequest.getCreatedByUsername().equals(contributor.username)){
                                            contributor.experience = issueExperience.calculateExperience(contributor);
                                            System.out.println(contributor.experience);
                                            break;
                                        }
                                    }
                                }

                            } else if ("deny".equals(decision)) {
                                selectedRequest.notifyObservers("Cererea ta "+ selectedRequest+ " a fost refuzata.", 1);
                                for(Regular regular : regularUsers){
                                    if (selectedRequest.getCreatedByUsername().equals(regular.getUsername())){
                                        selectedRequest.removeObserver(regular);
                                        break;
                                    }
                                }
                                for(Contributor contributor : contributors){
                                    if(selectedRequest.getCreatedByUsername().equals(contributor.getUsername())){
                                        selectedRequest.removeObserver(contributor);
                                        break;
                                    }
                                }
                                admin.assignedRequests.remove(selectedRequest);
                                requests.remove(selectedRequest);
                                System.out.println("Cerere refuzata.");
                            }
                        } else {
                            System.out.println("Decizie invalida. Va rugam introduceti 'accept' sau 'deny'.");
                        }
                    }
                    else if (requestIndex > assignedAdminRequests.size() && requestIndex <= assignedAdminRequests.size() + globalAdminRequests.size()) {
                        Request selectedRequest = globalAdminRequests.get(requestIndex - assignedAdminRequests.size() - 1);
                        System.out.println("Doriti sa acceptati sau sa refuzati cererea? (Introduceti 'accept' sau 'deny'):");
                        String decision = scanner.nextLine().toLowerCase();

                        if ("accept".equals(decision) || "deny".equals(decision)) {
                            if ("accept".equals(decision)) {
                                selectedRequest.notifyObservers("Cererea ta "+selectedRequest+ " a fost acceptata si va fi rezolvata curand!", 1);
                                for(Regular regular : regularUsers){
                                    if (selectedRequest.getCreatedByUsername().equals(regular.getUsername())){
                                        selectedRequest.removeObserver(regular);
                                        break;
                                    }
                                }
                                for(Contributor contributor : contributors){
                                    if(selectedRequest.getCreatedByUsername().equals(contributor.getUsername())){
                                        selectedRequest.removeObserver(contributor);
                                        break;
                                    }
                                }
                                admin.assignedRequests.remove(selectedRequest);
                                Admin.RequestHolder.removeRequest(selectedRequest);
                                requests.remove(selectedRequest);
                                System.out.println("Cerere acceptata!");
                                if(selectedRequest.getRequestType().equals(Request.RequestType.MOVIE_ISSUE) || selectedRequest.getRequestType().equals(Request.RequestType.ACTOR_ISSUE)){
                                    for(Regular regular : regularUsers){
                                        if(selectedRequest.getCreatedByUsername().equals(regular.username)){
                                            regular.experience = issueExperience.calculateExperience(regular);
                                            System.out.println(regular.experience);
                                            break;
                                        }
                                    }
                                    for(Contributor contributor : contributors){
                                        if(selectedRequest.getCreatedByUsername().equals(contributor.username)){
                                            contributor.experience = issueExperience.calculateExperience(contributor);
                                            System.out.println(contributor.experience);
                                            break;
                                        }
                                    }
                                }

                            } else if ("deny".equals(decision)) {
                                selectedRequest.notifyObservers("Cererea ta "+selectedRequest+" a fost refuzata.", 1);
                                for(Regular regular : regularUsers){
                                    if (selectedRequest.getCreatedByUsername().equals(regular.getUsername())){
                                        selectedRequest.removeObserver(regular);
                                        break;
                                    }
                                }
                                for(Contributor contributor : contributors){
                                    if(selectedRequest.getCreatedByUsername().equals(contributor.getUsername())){
                                        selectedRequest.removeObserver(contributor);
                                        break;
                                    }
                                }
                                admin.assignedRequests.remove(selectedRequest);
                                requests.remove(selectedRequest);
                                System.out.println("Cerere refuzata.");
                            }
                        } else {
                            System.out.println("Decizie invalida. Va rugam introduceti 'accept' sau 'deny'.");
                        }
                    } else {
                        System.out.println("Index cerere invalid.");
                    }
                    break;
                default:
                    System.out.println("Optiune invalida. Incercati din nou.");
                    break;
            }
        }
    }

    private void displayOptionsRegular() {
        System.out.println("Alege Actiune:");
        System.out.println("1.Vezi detalii de productie");
        System.out.println("2.Vezi detalii despre actori");
        System.out.println("3.Vezi notificari");
        System.out.println("4.Cauta actor/film/serial");
        System.out.println("5.Adauga/Sterge actor/film/serial de la favorite");
        System.out.println("6.Creare/Retragere cerere");
        System.out.println("7.Adaugare/Stergere recenzie");
        System.out.println("8.Delogare");
    }

    private void displayOptionsContributor() {
        System.out.println("Alege Actiune:");
        System.out.println("1.Vezi detalii de productie");
        System.out.println("2.Vezi detalii despre actori");
        System.out.println("3.Vezi notificari");
        System.out.println("4.Cauta actor/film/serial");
        System.out.println("5.Adauga/Sterge actor/film/serial de la favorite");
        System.out.println("6.Creare/Retragere cerere");
        System.out.println("7.Adauga/Sterge Actor/Productie din sistem");
        System.out.println("8.Delogare");
        System.out.println("9.Actualizare informatii despre productie/actor");
        System.out.println("10.Vizualizare cereri");

    }

    private void displayOptionsAdmin() {
        System.out.println("Alege Actiune:");
        System.out.println("1.Vezi detalii de productie");
        System.out.println("2.Vezi detalii despre actori");
        System.out.println("3.Vezi notificari");
        System.out.println("4.Cauta actor/film/serial");
        System.out.println("5.Adauga/Sterge actor/film/serial de la favorite");
        System.out.println("6.Adauga/Sterge utilizator");
        System.out.println("7.Adauga/Sterge Actor/Productie din sistem");
        System.out.println("8.Delogare");
        System.out.println("9.Actualizare informatii despre productie/actor");
        System.out.println("10.Vizualizare cereri");
    }

    public List<Regular> getRegularUsers() {
        return regularUsers;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public void removeUser(Scanner scanner){
        List<Contributor> con = new ArrayList<>(contributors);
        List<Regular> reg = new ArrayList<>(regularUsers);
        List<Request> requ = new ArrayList<>(requests);
        List<Movie> mov = new ArrayList<>(movies);
        List<Series> ser = new ArrayList<>(series);
        for(Regular regular : getRegularUsers()){
            System.out.println("Regular: " + regular.getUsername());
        }
        for(Contributor contributor: getContributors()){
            System.out.println("Contributor: " + contributor.getUsername());
        }

        System.out.println("Introduceti numele utilizatorului");
        String userDelete = scanner.nextLine();

        for(Regular regular : reg){
            if(regular.getUsername().equals(userDelete)){
                for (Request request1 : requ) {
                    if (request1.getCreatedByUsername().equals(regular.getUsername())) {
                        regular.deleteRequest(request1);
                        requests.remove(request1);
                    }
                }
                for(Movie movie : mov){
                    if(regular.reviewedProductions.contains(movie.getTitle())){
                        for(Rating rating : movie.ratings){
                            if(rating.getUsername().equals(regular.getUsername())){
                                movie.removeRating(regular.getUsername());
                                break;
                            }
                        }
                    }
                }
                for(Series series : ser){
                    if(regular.reviewedProductions.contains(series.getTitle())){
                        for(Rating rating : series.ratings){
                            if(rating.getUsername().equals(regular.getUsername())){
                                series.removeRating(regular.getUsername());
                                break;
                            }
                        }
                    }
                }
                regularUsers.remove(regular);
                System.out.println("Utilizatorul a fost șters cu succes din lista de Regular.");
                return;
            }
        }
        for(Contributor contributor : con){
            if(contributor.getUsername().equals(userDelete)){
                for (Comparable<?> element : contributor.userCreated) {
                    for(Admin admin : admins){
                        admin.userCreated.add(element);
                    }
                }
                for(Request request : requ){
                    if(request.getCreatedByUsername().equals(contributor.getUsername())){
                        contributor.deleteRequest(request);
                        requests.remove(request);
                    }
                }
                contributors.remove(contributor);
                System.out.println("Utilizatorul a fost șters cu succes din lista de Contributor.");
                return;
            }
        }

        System.out.println("Utilizatorul nu a fost gasit");

    }
    public void registerNewUser(Scanner scanner){
        System.out.println("Introduceți informațiile pentru noul utilizator:");

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Nume: ");
        String name = scanner.nextLine();

        String strongPassword = generateStrongPassword(name);

        System.out.print("Țară: ");
        String country = scanner.nextLine();

        System.out.print("Vârstă: ");
        int age = scanner.nextInt();
        scanner.nextLine();

        char gender;
        while (true) {
            try {
                System.out.print("Gen (M/F): ");
                String genderInput = scanner.nextLine().toUpperCase();

                if (genderInput.equals("M") || genderInput.equals("F")) {
                    gender = genderInput.charAt(0);
                    break;
                } else {
                    throw new InvalidCommandException("Opțiune invalidă pentru gen.");
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            }
        }


        LocalDateTime birthdate = LocalDateTime.now();

        User.Information userInfo = new User.Information(
                new Credentials(email, strongPassword),
                name, country, age, gender, birthdate
        );

        System.out.println("Alegeți tipul de cont:");
        System.out.println("1. Regular");
        System.out.println("2. Contributor");
        System.out.println("3. Admin");
        int accountTypeChoice = scanner.nextInt();
        scanner.nextLine();

        AccountType accountType;
        switch (accountTypeChoice) {
            case 1:
                accountType = AccountType.REGULAR;
                break;
            case 2:
                accountType = AccountType.CONTRIBUTOR;
                break;
            case 3:
                accountType = AccountType.ADMIN;

                break;
            default:
                System.out.println("Opțiune invalidă. Se creează un utilizator regulat implicit.");
                accountType = AccountType.REGULAR;
        }

        User newUser = UserFactory.createUser(userInfo, accountType);

        switch (accountType) {
            case REGULAR:
                regularUsers.add((Regular) newUser);
                newUser.setExperience(0);
                newUser.setFavorites(new TreeSet<>());
                newUser.setNotifications(new ArrayList<>());
                break;
            case CONTRIBUTOR:
                contributors.add((Contributor) newUser);
                newUser.setExperience(0);
                newUser.setFavorites(new TreeSet<>());
                newUser.setNotifications(new ArrayList<>());
                break;
            case ADMIN:
                admins.add((Admin) newUser);
                newUser.setExperience(0);
                newUser.setFavorites(new TreeSet<>());
                newUser.setNotifications(new ArrayList<>());
                break;
        }
        System.out.println("Verificați informațiile:");
        System.out.println(newUser);
        System.out.print("Sunt informațiile corecte? (Da/Nu): ");
        String verificationChoice = scanner.nextLine().toUpperCase();

        if (!verificationChoice.equalsIgnoreCase("Da")) {
            registerNewUser(scanner);
        } else {
            System.out.println("Utilizatorul a fost creat cu succes!");
            System.out.println("Parola generată pentru dumneavoastră: " + strongPassword + "\n");
        }
    }

    private String generateStrongPassword(String name) {
        String[] nameParts = name.split(" ");
        String password = nameParts[0].substring(0, 1) + "@" + nameParts[nameParts.length - 1] + "!#"
                + (new SecureRandom().nextInt(100));

        return password;
    }


    public List<Actor> getActors() {
        return actors;
    }

    public List<Movie> getMovies(){
        return movies;
    }

    public List<Series> getSeries(){
        return series;
    }

    public static void main(String[] args) {
        IMDB imdb = IMDB.getInstance();
        imdb.run();

    }

}
