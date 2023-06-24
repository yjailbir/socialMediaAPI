package program.api.socialapi.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import program.api.socialapi.entities.Post;
import program.api.socialapi.entities.User;
import program.api.socialapi.security.jwt.JwtTokenProvider;
import program.api.socialapi.service.PostService;
import program.api.socialapi.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Api(tags = "Posts management")
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public PostController(PostService postService, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.postService = postService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiOperation(value = "Creates a new post and saves it to the database", consumes = "multipart/form-data")
    @PostMapping("/create")
    public ResponseEntity<HashMap<Object, Object>> createPost(
            HttpServletRequest request,
            @ApiParam(value = "Image for post (file)") @RequestParam(value = "image", required = false) MultipartFile image,
            @ApiParam(value = "Title for post (text)") @RequestParam(value = "title", required = false) String title,
            @ApiParam(value = "Text for post (text)") @RequestParam(value = "text", required = false) String text
    ) throws IOException {
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        String token = request.getHeader("Authorization");

        User user = userService.findByUsername(jwtTokenProvider.getUsername(token));

        Post post = new Post();
        if(title != null)
            post.setTitle(title);
        if(text != null)
            post.setText(text);
        if(image != null && !image.isEmpty())
            post.setImage(image.getBytes());
        post.setUser_id(user.getId());
        post.setCreated_at(Date.from(Instant.now()));
        Post savedPost = postService.createPost(post);
        response.put("result", "ok");
        responseContent.put("createdPost", savedPost);
        response.put("response", responseContent);

        return ResponseEntity.ok(response);
    }

    // Просмотр постов доступен всем, в том числе не авторизованным пользователям
    @ApiOperation("Shows all posts of the selected user")
    @GetMapping("/by/{username}")
    public ResponseEntity<HashMap<Object, Object>> showPosts(
            @ApiParam(value = "The username of the author whose posts user want to view") @PathVariable("username") String username
    ){
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        if(userService.findByUsername(username) != null){
            List<Post> posts = postService.findByAuthor(username);

            if(posts.size() != 0){
                response.put("result", "ok");
                responseContent.put("posts", posts);
                response.put("response", responseContent);
            }
            else {
                response.put("result", "ok");
                response.put("response", "The author has not yet created any posts");
            }
            return ResponseEntity.ok(response);
        }
        else {
            response.put("result", "error");
            response.put("response", "User not found");

            return ResponseEntity.badRequest().body(response);
        }
    }

    @ApiOperation("Deletes the selected post from the database")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<HashMap<Object, Object>> deletePost(
            HttpServletRequest request,
            @ApiParam(value = "Id of the post the user wants to delete") @PathVariable("id") Integer id
    ){
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        String token = request.getHeader("Authorization");

        try {
            if (jwtTokenProvider.getAuthentication(token).isAuthenticated()){
                User user = userService.findByUsername(jwtTokenProvider.getUsername(token));

                Post postToDelete = postService.findById(id);

                if(postToDelete.getUser_id().equals(user.getId())){
                    postService.deleteById(id);
                    response.put("result", "ok");
                    responseContent.put("deleted_post", postToDelete);
                    response.put("response", responseContent);

                    return ResponseEntity.ok(response);
                }
                else {
                    response.put("result", "error");
                    response.put("response", "Wrong post id");

                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        catch (NullPointerException e){
            response.put("result", "error");
            response.put("response", "Wrong post id");

            return ResponseEntity.badRequest().body(response);
        }

        return null;
    }


    @ApiOperation(value = "Changes the selected post and saves the changes in the database", consumes = "multipart/form-data")
    @PostMapping("/edit/{id}") // С PatchMapping почему-то выдаёт ошибку
    public ResponseEntity<HashMap<Object, Object>> editPost (
        @PathVariable("id") Integer id,
        HttpServletRequest request,
        @ApiParam(value = "Image for updated post (file)") @RequestParam(value = "image", required = false) MultipartFile image,
        @ApiParam(value = "Title for updated post (text)") @RequestParam(value = "title", required = false) String title,
        @ApiParam(value = "Text for updated post (text)") @RequestParam(value = "text", required = false) String text
        ) throws IOException {
        HashMap<Object, Object> response = new HashMap<>();
        HashMap<Object, Object> responseContent = new HashMap<>();

        String token = request.getHeader("Authorization");

        try {
            User user = userService.findByUsername(jwtTokenProvider.getUsername(token));

            Post postToUpdate = postService.findById(id);

            if(postToUpdate.getUser_id().equals(user.getId())){
                if(image != null && !image.isEmpty())
                    postToUpdate.setImage(image.getBytes());
                if(title != null)
                    postToUpdate.setTitle(title);
                if(text != null)
                    postToUpdate.setText(text);
            }

            postService.updatePost(postToUpdate);
            response.put("result", "ok");
            responseContent.put("updated_post", postToUpdate);
            response.put("response", responseContent);
        }
        catch (NullPointerException e){
            response.put("result", "error");
            response.put("response", "Wrong post id");

            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
