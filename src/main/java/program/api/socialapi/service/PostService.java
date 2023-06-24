package program.api.socialapi.service;

import program.api.socialapi.entities.Post;
import program.api.socialapi.entities.User;

import java.util.List;

public interface PostService {
    Post createPost(Post post);
    List<Post> getAll();
    List<Post> findByAuthor(String author);
    void deleteById(Integer id);
    void updatePost(Post updatedPost);
    Post findById(Integer id);
    List<Post> getFeed(List<User> subscriptions);
}
