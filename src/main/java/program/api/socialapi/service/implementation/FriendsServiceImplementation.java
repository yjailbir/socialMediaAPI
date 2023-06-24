package program.api.socialapi.service.implementation;

import org.springframework.stereotype.Service;
import program.api.socialapi.entities.Friends;
import program.api.socialapi.entities.User;
import program.api.socialapi.repository.FriendsRepository;
import program.api.socialapi.repository.UserRepository;
import program.api.socialapi.service.FriendsService;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FriendsServiceImplementation implements FriendsService {

    private final FriendsRepository friendsRepository;
    private final UserRepository userRepository;

    public FriendsServiceImplementation(FriendsRepository friendsRepository, UserRepository userRepository) {
        this.friendsRepository = friendsRepository;
        this.userRepository = userRepository;
    }


    @Override
    public List<String> getUserFriends(User user) {
        return friendsRepository
                .findAll()
                .stream()
                .filter(friends -> friends.getUser1_id().equals(user.getId()) || friends.getUser2_id().equals(user.getId()))
                .map(friends -> {
                    if (friends.getUser1_id().equals(user.getId())) {
                        return Objects.requireNonNull(userRepository.findById(friends.getUser2_id()).orElse(null)).getUsername();
                    } else {
                        return Objects.requireNonNull(userRepository.findById(friends.getUser1_id()).orElse(null)).getUsername();
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public void confirmFriendship(User user1, User user2) {
        Friends friends = new Friends();
        friends.setUser1_id(user1.getId());
        friends.setUser2_id(user2.getId());

        friendsRepository.save(friends);
    }

    @Override
    public void delete(User user1, User user2) {
        List<Friends> friendsList = friendsRepository.findAll();

        for(Friends friends: friendsList){
            if (
                    friends.getUser1_id().equals(user1.getId()) &&
                            friends.getUser2_id().equals(user2.getId())
            ){
                friendsRepository.delete(friends);
                break;
            }
        }
    }
}
