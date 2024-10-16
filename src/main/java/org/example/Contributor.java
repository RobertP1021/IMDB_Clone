package org.example;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Contributor extends Staff implements RequestsManager {

    public Contributor(Information userInfo) {
        super(userInfo, AccountType.CONTRIBUTOR);
    }


    public void addFavorite(Comparable<?> item) {
        favorites.add(item);
    }

    public void removeFavorite(Comparable<?> item) {
        favorites.remove(item);
    }

    @Override
    public void updateExperience() {
        experience += 300;
    }

    public void setExperience(int experience){
        this.experience = experience;
    }
    @Override
    public void logout() {
        System.out.println("Contributor User logged out.");
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
                        admin.assignedRequests.add(request);
                        request.registerObserver(admin);
                        request.notifyObservers("Aveti o cerere noua", 1);
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
                        break;
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
                        break;
                    }
                }
                if(ok1 == false){
                    System.out.println("Nu a fost gasit creatorul.");
                }
                break;
            case 3:
                request.setRequestType(Request.RequestType.DELETE_ACCOUNT);
                request.setAssignedToUsername("ADMIN");
                Admin.RequestHolder.addRequest(request);
                for(Admin admin : admins){
                    request.registerObserver(admin);
                    request.notifyObservers("Aveti o cerere noua", 1);
                }
                System.out.println("Cererea a fost adaugata cu succes!");
                break;
            case 4:
                request.setRequestType(Request.RequestType.OTHERS);
                request.setAssignedToUsername("ADMIN");
                Admin.RequestHolder.addRequest(request);
                for(Admin admin : admins){
                    request.registerObserver(admin);
                    request.notifyObservers("Aveti o cerere noua", 1);
                }
                System.out.println("Cererea a fost adaugata cu succes!");
                break;
            default:
                System.out.println("Opțiune invalidă. Încercați din nou.");
                break;
        }
    }

    public List<String> getNotifications() {
        return userNotifications;
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

    public List<Request> getAssignedRequests() {
        System.out.println("Cereri pentru acest cont: ");
        return assignedRequests;
    }

    public void addProductionSystem(Production production) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduceti informatiile pentru noua productie:");

        System.out.println("Titlu:");
        String title = scanner.nextLine();
        production.setTitle(title);

        List<String> directors = new ArrayList<>();
        List<String> actors = new ArrayList<>();
        List<Genre> genres = new ArrayList<>();
        String plot;

        System.out.println("Introduceti regizorii (introduceti 'done' cand ati terminat):");
        String director;
        while (!(director = scanner.nextLine()).equalsIgnoreCase("done")) {
            directors.add(director);
            production.setDirectors(directors);
        }

        System.out.println("Introduceti actorii (introduceti 'done' cand ati terminat):");
        String actor;
        while (!(actor = scanner.nextLine()).equalsIgnoreCase("done")) {
            actors.add(actor);
            production.setActors(actors);
        }

        System.out.println("Introduceti genurile (introduceti 'done' cand ati terminat):");
        String genreStr;
        while (!(genreStr = scanner.nextLine()).equalsIgnoreCase("done")) {
            try {
                Genre genre = Genre.valueOf(genreStr);
                genres.add(genre);
                production.setGenres(genres);
            } catch (IllegalArgumentException e) {
                System.out.println("Genul introdus nu este valid. Incercati din nou.");
            }
        }

        System.out.println("Introduceti descrierea:");
        plot = scanner.nextLine();
        production.setPlotDescription(plot);

    }

    public void removeProductionSystem(String name) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Alege optiunea:\n1.Sterge film\n2.Sterge serial");
        int stergere = scanner.nextInt();
        scanner.nextLine();

        switch (stergere) {
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
                    if (userCreated.contains(searchMovie)) {
                        movies.remove(searchMovie);
                        userCreated.remove(searchMovie);
                        System.out.println("Filmul a fost sters cu succes!");
                    } else {
                        System.out.println("Nu aveti permisiunea de a sterge acest film.");
                    }
                } else {
                    System.out.println("Filmul nu a fost gasit.");
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
                    if (userCreated.contains(searchSeries)) {
                        series.remove(searchSeries);
                        userCreated.remove(searchSeries);
                        System.out.println("Serialul a fost sters cu succes!");
                    } else {
                        System.out.println("Nu aveti permisiunea de a sterge acest serial.");
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

    public void addActorSystem(Actor actor) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduceti informatiile pentru noul actor:");

        System.out.println("Nume:");
        String name = scanner.nextLine();
        actor.setName(name);

        System.out.println("Biografia:");
        String biography = scanner.nextLine();
        actor.setBiography(biography);

        System.out.println("Performante:");
        List<Actor.NameTypePair> roles = new ArrayList<>();
        boolean addMoreRoles = true;

        while (addMoreRoles) {
            System.out.print("Introduceți numele performanței: ");
            String roleName = scanner.nextLine();

            try {
                System.out.print("Introduceți tipul performanței (Movie/Series): ");
                String roleType = scanner.nextLine().toLowerCase();
                roleType = roleType.substring(0, 1).toUpperCase() + roleType.substring(1);

                if (!roleType.equals("Movie") && !roleType.equals("Series")) {
                    throw new IllegalArgumentException("Tipul performanței trebuie să fie 'Movie' sau 'Series'.");
                }


                Actor.NameTypePair role = new Actor.NameTypePair(roleName, roleType);
                actor.setRoles(roles);
                roles.add(role);

                System.out.print("Doriți să adăugați încă o performanță? (Da/Nu): ");
                String addMore = scanner.nextLine().toLowerCase();
                addMoreRoles = addMore.equals("da");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        userCreated.add(actor);
        System.out.println("Actorul a fost creat cu succes!");
    }

    public void removeActorSystem(String name) {
        List<Actor> actors = IMDB.getInstance().getActors();
        Actor searchActor = null;
        for (Actor actor : actors) {
            if (actor.getName().equalsIgnoreCase(name)) {
                searchActor = actor;
                break;
            }
        }

        if (searchActor != null) {
            if (userCreated.contains(searchActor)) {
                actors.remove(searchActor);
                userCreated.remove(searchActor);
                System.out.println("Actorul a fost sters cu succes!");
            } else {
                System.out.println("Nu aveti permisiunea de a sterge acest actor.");
            }
        } else {
            System.out.println("Actorul nu a fost gasit.");
        }
    }

    public void updateProduction(Production production) {
        Production existingProduction = findProductionByName(production.getTitle());
        if (existingProduction != null) {
            existingProduction.setTitle(production.getTitle());
            existingProduction.setDirectors(production.getDirectors());
            existingProduction.setActors(production.getActors());
            existingProduction.setGenres(production.getGenres());
            existingProduction.setRatings(production.getRatings());
            existingProduction.setPlotDescription(production.getPlotDescription());
            System.out.println("Production info updated: " + production.getTitle());
        } else {
            System.out.println("Production not found: " + production.getTitle());
        }
    }

    public void updateActor(Actor actor) {
        Actor existingActor = findActorByName(actor.getName());
        if (existingActor != null) {
            existingActor.setName(actor.getName());
            existingActor.setRoles(actor.getRoles());
            existingActor.setBiography(actor.getBiography());
            System.out.println("Actor info updated: " + actor.getName());
        } else {
            System.out.println("Actor not found: " + actor.getName());
        }
    }

    public void resolveRequests(List<Request> requests) {
        for (Request request : requests) {
            assignedRequests.remove(request);
            notifications.add("Request resolved: " + request.toString());
            System.out.println("Request resolved: " + request.toString());
        }
    }

    private Production findProductionByName(String name) {
        return favorites.stream()
                .filter(item -> item instanceof Production && ((Production) item).getTitle().equals(name))
                .map(item -> (Production) item)
                .findFirst()
                .orElse(null);
    }

    private Actor findActorByName(String name) {
        return favorites.stream()
                .filter(item -> item instanceof Actor && ((Actor) item).getName().equals(name))
                .map(item -> (Actor) item)
                .findFirst()
                .orElse(null);
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
}
