package culturelog.rest.repository;

import culturelog.rest.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Jan Venstermans
 */
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user object by unique username.
     * @param username
     * @return
     */
    User findByUsername(String username);

}
