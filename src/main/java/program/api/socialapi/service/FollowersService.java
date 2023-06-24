package program.api.socialapi.service;

import program.api.socialapi.entities.Followers;
import program.api.socialapi.entities.User;

import java.util.List;

public interface FollowersService {
    void subscribe(User sender, User receiver);
    List<Followers> getAll();
    List<String> getAllUserSubscribers(User user);
    void unsubscribe(User user1, User user2);

    List<User> getUserSubscriptions(User user);
}
