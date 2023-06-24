package program.api.socialapi.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import program.api.socialapi.entities.Followers;
import program.api.socialapi.entities.User;
import program.api.socialapi.repository.FollowersRepository;
import program.api.socialapi.service.FollowersService;
import program.api.socialapi.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowersServiceImplementation implements FollowersService {

    private final FollowersRepository followersRepository;
    private final UserService userService;

    @Autowired
    public FollowersServiceImplementation(FollowersRepository followersRepository, UserService userService){
        this.followersRepository = followersRepository;
        this.userService = userService;
    }


    @Override
    public void subscribe(User sender, User receiver) {
        Followers followers = new Followers();
        followers.setSender_id(sender.getId());
        followers.setReceiver_id(receiver.getId());

        followersRepository.save(followers);
    }

    @Override
    public List<Followers> getAll() {
        return followersRepository.findAll();
    }

    @Override
    public List<String> getAllUserSubscribers(User user) {

        return getAll()
                .stream()
                .filter(follower -> follower.getReceiver_id().equals(user.getId()))
                .map(follower -> userService.findById(follower.getSender_id()).getUsername())
                .collect(Collectors.toList());
        }

    @Override
    public void unsubscribe(User user1, User user2) {
        Followers followers = new Followers();
        followers.setSender_id(user1.getId());
        followers.setReceiver_id(user2.getId());

        followersRepository.delete(followers);
    }

    @Override
    public List<User> getUserSubscriptions(User user) {

        return getAll()
                .stream()
                .filter(followers -> followers.getSender_id().equals(user.getId()))
                .map(followers -> userService.findById(followers.getReceiver_id()))
                .collect(Collectors.toList());
    }
}
