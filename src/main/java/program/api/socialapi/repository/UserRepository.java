package program.api.socialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import program.api.socialapi.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String name);
}
