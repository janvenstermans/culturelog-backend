package culturelog.rest.repository;

import culturelog.rest.domain.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jan Venstermans
 */
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

}
