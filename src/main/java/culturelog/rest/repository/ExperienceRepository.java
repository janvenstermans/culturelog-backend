package culturelog.rest.repository;

import culturelog.rest.domain.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Jan Venstermans
 */
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    /**
     * Find all experiences of o a user
     * @param username
     * @return
     */
    List<Experience> findByUsername(String username);
}
