package program.api.socialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import program.api.socialapi.entities.Friends;

public interface FriendsRepository extends JpaRepository<Friends, Integer> {
}
