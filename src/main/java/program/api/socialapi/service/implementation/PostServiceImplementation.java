package program.api.socialapi.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import program.api.socialapi.entities.Post;
import program.api.socialapi.entities.User;
import program.api.socialapi.repository.PostRepository;
import program.api.socialapi.repository.UserRepository;
import program.api.socialapi.service.PostService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImplementation implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostServiceImplementation(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> findByAuthor(String username) {
        return postRepository
                .findAll()
                .stream()
                .filter(post -> post.getUser_id().equals(userRepository.findByUsername(username).getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        postRepository.deleteById(id);
    }

    @Override
    public void updatePost(Post updatedPost) {
        Post existingPost = postRepository.findById(updatedPost.getId()).orElse(null);

        assert existingPost != null;
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setText(updatedPost.getText());
        existingPost.setImage(updatedPost.getImage());

        postRepository.save(existingPost);
    }

    @Override
    public Post findById(Integer id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public List<Post> getFeed(List<User> subscriptions) {
        List<Post> res = new ArrayList<>();

        for (User user: subscriptions)
            res.addAll(findByAuthor(user.getUsername()));

        Comparator<Post> comparator = Comparator.comparing(Post::getCreated_at).reversed();

        res.sort(comparator);

        return res;
    }
}
