package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin extends Staff {

    public static class RequestHolder {

        private static List<Request> requests = new ArrayList<>();

        public static void addRequest(Request request) {

            requests.add(request);
        }

        public static void removeRequest(Request request) {

            requests.remove(request);
        }

        public static List<Request> getRequests() {
            System.out.println("Cereri pentru toti adminii: ");
            return requests;
        }
    }

    public Admin(Information userInfo) {
        super(userInfo, AccountType.ADMIN);
    }

    public List<Request> getAssignedRequests() {
        System.out.println("Cereri pentru acest cont: ");
        return  assignedRequests;
    }
    public void addUserToSystem(User user) {
        UserFactory.createUser(user.getInformation(),user.accountType);
    }

    public void removeUserFromSystem() {

    }

    public void resolveRequests(List<Request> requests) {
        for (Request request : requests) {
            assignedRequests.remove(request);
        }
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

        userCreated.add(production);
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
        Scanner scanner = new Scanner(System.in);
        boolean updateAnotherField = true;
        while (updateAnotherField) {
            System.out.println("Alegeti campul pe care doriti sa-l actualizati:");
            System.out.println("1. Titlu");
            System.out.println("2. Regizori");
            System.out.println("3. Actorii");
            System.out.println("4. Genuri");
            System.out.println("5. Descriere");

            int choice = -1;

            while (choice < 1 || choice > 7) {
                System.out.print("Introduceti numarul corespunzator campului pe care doriti sa-l actualizati: ");
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Va rugam introduceti un numar valid.");
                }
            }

            switch (choice) {
                case 1:
                    System.out.println("Noul titlu:");
                    production.setTitle(scanner.nextLine());
                    break;
                case 2:
                    System.out.println("Introduceti regizorii (introduceti 'done' cand ati terminat):");
                    List<String> newDirectors = new ArrayList<>();
                    String director;
                    while (!(director = scanner.nextLine()).equalsIgnoreCase("done")) {
                        newDirectors.add(director);
                    }
                    production.setDirectors(newDirectors);
                    break;
                case 3:
                    System.out.println("Introduceti actorii (introduceti 'done' cand ati terminat):");
                    List<String> newActors = new ArrayList<>();
                    String actor;
                    while (!(actor = scanner.nextLine()).equalsIgnoreCase("done")) {
                        newActors.add(actor);
                    }
                    production.setActors(newActors);
                    break;
                case 4:
                    System.out.println("Introduceti genurile (introduceti 'done' cand ati terminat):");
                    List<Genre> newGenres = new ArrayList<>();
                    String genreStr;
                    while (!(genreStr = scanner.nextLine()).equalsIgnoreCase("done")) {
                        try {
                            Genre genre = Genre.valueOf(genreStr.toUpperCase());
                            newGenres.add(genre);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Genul introdus nu este valid. Incercati din nou.");
                        }
                    }
                    production.setGenres(newGenres);
                    break;
                case 5:
                    System.out.println("Noua descriere:");
                    production.setPlotDescription(scanner.nextLine());
                    break;

                default:
                    System.out.println("Optiune invalida.");
                    break;
            }

            System.out.print("Doriti sa actualizati si alte campuri? (Y/N): ");
            String updateAnother = scanner.nextLine().trim().toUpperCase();
            updateAnotherField = updateAnother.equals("Y");
        }
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

    @Override
    public String toString() {
        return "Admin{" +
                "userInfo=" + userInfo +
                "\naccountType=" + accountType +
                "\nusername='" + username + '\'' +
                "\nexperience=" + experience +
                "\nnotifications=" + notifications +
                "\nfavorites=" + favorites +
                '}';
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

    public List<String> getNotifications() {
        return userNotifications;
    }

}