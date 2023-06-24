package program.api.socialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import program.api.socialapi.entities.Followers;

public interface FollowersRepository extends JpaRepository<Followers, Integer> {
}
