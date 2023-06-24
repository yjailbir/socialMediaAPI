package program.api.socialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import program.api.socialapi.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
}
