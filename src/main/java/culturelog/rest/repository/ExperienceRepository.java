package culturelog.rest.repository;

import culturelog.rest.domain.Experience;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Jan Venstermans
 */
public interface ExperienceRepository extends MongoRepository<Experience, String> {

    /**
     * Find all experiences of o a user
     * @param username
     * @return
     */
    List<Experience> findByUsername(String username);
}
