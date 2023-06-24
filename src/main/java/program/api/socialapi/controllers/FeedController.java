package program.api.socialapi.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import program.api.socialapi.dto.PostDTO;
import program.api.socialapi.entities.Post;
import program.api.socialapi.entities.User;
import program.api.socialapi.security.jwt.JwtTokenProvider;
import program.api.socialapi.service.FollowersService;
import program.api.socialapi.service.PostService;
import program.api.socialapi.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Api(tags = "Newsfeed management")
@RestController
@RequestMapping("api/feed")
public class FeedController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final FollowersService followersService;
    private final PostService postService;

    @Autowired
    public FeedController(JwtTokenProvider jwtTokenProvider, UserService userService, FollowersService followersService, PostService postService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.followersService = followersService;
        this.postService = postService;
    }

    @ApiOperation(value = "Returns the user's news feed from old news to new news")
    @GetMapping
    public ResponseEntity<HashMap<Object, Object>> showFeed(HttpServletRequest request){
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        String token = request.getHeader("Authorization");

        User user = userService.findByUsername(jwtTokenProvider.getUsername(token));

        List<User> subscriptions = followersService.getUserSubscriptions(user);

        List<Post> feed = postService.getFeed(subscriptions);
        List<PostDTO> feedView = new ArrayList<>();

        for(Post post: feed){
            PostDTO temp = new PostDTO();
            temp.setAuthor(userService.findById(post.getUser_id()).getUsername());
            temp.setCreated_at(post.getCreated_at());
            temp.setTitle(post.getTitle());
            temp.setText(post.getText());
            temp.setImage(post.getImage());

            feedView.add(temp);
        }

        response.put("result", "ok");
        responseContent.put("feed", feedView);
        response.put("response", responseContent);

        return ResponseEntity.ok(response);
    }
}
