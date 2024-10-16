package org.example;

import java.util.ArrayList;
import java.util.List;

public abstract class Staff extends User implements StaffInterface {

    public List<Comparable<?>> userCreated;
    public List<Request> assignedRequests;

    public Staff(Information userInfo, AccountType accountType) {
        super(userInfo, accountType);
        this.assignedRequests = new ArrayList<>();
        this.userCreated = new ArrayList<>();
    }


    public List<Comparable<?>> getUserCreated() {
        return userCreated;
    }

    @Override
    public void addFavorite(Comparable<?> item) {
        favorites.add(item);

    }
    public void addFavoriteProduction(Production production){
        favorites.add(production);
    }

    @Override
    public void removeFavorite(Comparable<?> item) {
        favorites.remove(item);
    }

    public void removeFavoriteProduction(Production production){
        favorites.remove(production);
    }

    @Override
    public void updateExperience() {
        experience += 200;
    }

    @Override
    public void logout() {
        System.out.println("Staff User logged out.");
    }

    public void addProductionSystem(Production production) {
        favorites.add(production);
    }

    public void removeProductionSystem(String name) {
        Production productionToRemove = findProductionByName(name);
        if (productionToRemove != null) {
            favorites.remove(productionToRemove);
            System.out.println("Production removed from system: " + name);
        } else {
            System.out.println("Production not found: " + name);
        }
    }

    public void addActorSystem(Actor actor) {
        favorites.add(actor);
    }

    public void removeActorSystem(String name) {
        Actor actorToRemove = findActorByName(name);
        if (actorToRemove != null) {
            favorites.remove(actorToRemove);
            System.out.println("Actor removed from system: " + name);
        } else {
            System.out.println("Actor not found: " + name);
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
}
