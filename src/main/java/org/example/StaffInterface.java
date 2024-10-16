package org.example;
import java.util.*;
public interface StaffInterface {

    void addProductionSystem(Production production);

    void addActorSystem(Actor actor);

    void removeProductionSystem(String name);

    void removeActorSystem(String name);

    void updateProduction(Production production);

    void updateActor(Actor actor);

    void resolveRequests(List<Request> requests);
}
