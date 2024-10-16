package org.example;

public class IssueExperience implements ExperienceStrategy{
    @Override
    public int calculateExperience(User user) {
        for(Contributor contributor : IMDB.getInstance().contributors){
            if(contributor.username.equals(user.username)){
                contributor.experience = user.experience + 200;
                return contributor.experience;
            }
        }
        for(Regular regular : IMDB.getInstance().regularUsers){
            if(regular.username.equals(user.username)){
                regular.experience = user.experience + 200;
                return regular.experience;
            }
        }
        return -1;
    }
}
