package program.api.socialapi.service;

import program.api.socialapi.entities.User;

import java.util.List;

public interface FriendsService {
    List<String> getUserFriends(User user);
    void confirmFriendship(User user1, User user2);
    void delete(User user1, User user2);
}
