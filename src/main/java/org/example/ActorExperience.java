package org.example;

public class ActorExperience implements ExperienceStrategy{
    @Override
    public int calculateExperience(User user) {
        for(Admin admin : IMDB.getInstance().admins){
            if(admin.username.equals(user.username)){
                admin.experience = user.experience + 100;
                return admin.experience;
            }
        }
        for(Contributor contributor : IMDB.getInstance().contributors){
            if(contributor.username.equals(user.username)){
                contributor.experience = user.experience + 100;
                return contributor.experience;
            }
        }
        return -1;
    }
}
