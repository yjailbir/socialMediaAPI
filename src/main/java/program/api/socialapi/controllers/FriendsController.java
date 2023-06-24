package program.api.socialapi.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import program.api.socialapi.entities.User;
import program.api.socialapi.security.jwt.JwtTokenProvider;
import program.api.socialapi.service.FollowersService;
import program.api.socialapi.service.FriendsService;
import program.api.socialapi.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Api(tags = "User's friends and followers management")
@RestController
@RequestMapping("api/friends")
public class FriendsController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final FollowersService followersService;
    private final FriendsService friendsService;

    @Autowired
    public FriendsController(JwtTokenProvider jwtTokenProvider, UserService userService, FollowersService followersService, FriendsService friendsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.followersService = followersService;
        this.friendsService = friendsService;
    }

    @ApiOperation("Sends the specified user a friendship request, turning the sender into the recipient's subscriber")
    @PostMapping("/follow/{username}")
    public ResponseEntity<HashMap<Object, Object>> subscribeUser(
           @ApiParam(value = "username to subscribe") @PathVariable("username") String username,
            HttpServletRequest request
    ){
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        String token = request.getHeader("Authorization");

        try {
            User sender = userService.findByUsername(jwtTokenProvider.getUsername(token));
            User receiver = userService.findByUsername(username);

            if(receiver != null){
                if(!followersService.getAllUserSubscribers(receiver).contains(sender.getUsername())){
                    followersService.subscribe(sender, receiver);

                    response.put("result", "ok");
                    responseContent.put("followed_user", receiver.getUsername());
                    response.put("response", responseContent);

                    return ResponseEntity.ok(response);
                }
                else {
                    response.put("result", "error");
                    response.put("response", "You are already flollow to the user " + receiver.getUsername());

                    return ResponseEntity.badRequest().body(response);
                }
            }
            else
                throw new UsernameNotFoundException("User with username " + username + " not found!");
        }
        catch (UsernameNotFoundException e){
            response.put("result", "error");
            response.put("response", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @ApiOperation("Returns the list of user's subscribers usernames")
    @GetMapping("/myFollowers")
    public ResponseEntity<HashMap<Object, Object>> getFollowers(HttpServletRequest request){
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        String token = request.getHeader("Authorization");

        User receiver = userService.findByUsername(jwtTokenProvider.getUsername(token));

        List<String> followers = followersService.getAllUserSubscribers(receiver);

        response.put("result", "ok");
        responseContent.put("followers", followers);
        response.put("response", responseContent);

        return ResponseEntity.ok(response);

    }

    @ApiOperation("Returns the list of user's friends usernames")
    @GetMapping("/myFriends")
    public ResponseEntity<HashMap<Object, Object>> getFriends(HttpServletRequest request){
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        String token = request.getHeader("Authorization");

        User user = userService.findByUsername(jwtTokenProvider.getUsername(token));

        List<String> friends = friendsService.getUserFriends(user);

        response.put("result", "ok");
        responseContent.put("friends", friends);
        response.put("response", responseContent);

        return ResponseEntity.ok(response);
    }

    @ApiOperation("Approves friendship requests from the specified user," +
            "turning the recipient and sender into friends, who are also each other's subscribers")
    @PostMapping("/approve/{username}")
    public ResponseEntity<HashMap<Object, Object>> approveFriendship(
           @ApiParam(value = "Username to confirm friendship") @PathVariable("username") String username,
            HttpServletRequest request
    ){
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        String token = request.getHeader("Authorization");

        try {
            User receiver = userService.findByUsername(jwtTokenProvider.getUsername(token));
            User sender = userService.findByUsername(username);

            if(sender == null)
                throw new UsernameNotFoundException("User with username " + username + " not found!");

            List<String> friends = friendsService.getUserFriends(receiver);
            List<String> followers = followersService.getAllUserSubscribers(receiver);

            if(followers.contains(username)){
                if(!friends.contains(username)){
                    friendsService.confirmFriendship(receiver, sender);
                    followersService.subscribe(receiver, sender);

                    response.put("result", "ok");
                    responseContent.put("confirmed_friend", sender.getUsername());
                    response.put("response", responseContent);

                    return ResponseEntity.ok(response);
                }
                else {
                    response.put("result", "error");
                    response.put("response", "You are already friends with " + username);

                    return ResponseEntity.badRequest().body(response);
                }
            }
            else {
                response.put("result", "error");
                response.put("response", "The user " + username + " did not send you a friendship request");

                return ResponseEntity.badRequest().body(response);
            }
        }
        catch (UsernameNotFoundException e){
            response.put("result", "error");
            response.put("response", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @ApiOperation("Deletes the specified user from his friends and unsubscribing from him")
    @DeleteMapping("/deleteFriend/{username}")
    public ResponseEntity<HashMap<Object, Object>> deleteFriend(
            @ApiParam(value = "Username to delete from friends list") @PathVariable("username") String username,
            HttpServletRequest request
    ){
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        String token = request.getHeader("Authorization");

        try {
            User user = userService.findByUsername(jwtTokenProvider.getUsername(token));
            User noMoreFriend = userService.findByUsername(username);

            if(noMoreFriend == null)
                throw new UsernameNotFoundException("User with username " + username + " not found!");

            List<String> friends = friendsService.getUserFriends(user);

            if(friends.contains(username)){
                friendsService.delete(user, noMoreFriend);
                followersService.unsubscribe(user, noMoreFriend);

                response.put("result", "ok");
                responseContent.put("deleted_friend", noMoreFriend.getUsername());
                response.put("response", responseContent);

                return ResponseEntity.ok(response);
            }
            else {
                response.put("result", "error");
                response.put("response", "You are not friends with " + username);

                return ResponseEntity.badRequest().body(response);
            }
        }
        catch (UsernameNotFoundException e){
            response.put("result", "error");
            response.put("response", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
