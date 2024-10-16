package org.example;

public class RatingExperience implements ExperienceStrategy {
    @Override
    public int calculateExperience(User user) {
        for(Regular regular : IMDB.getInstance().regularUsers){
            if(regular.username.equals(user.username)){
                regular.experience = user.experience + 50;
                return regular.experience;
            }
        }
        return -1;
    }
}
