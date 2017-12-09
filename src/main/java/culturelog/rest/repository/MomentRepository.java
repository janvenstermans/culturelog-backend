package culturelog.rest.repository;

import culturelog.rest.domain.Moment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jan Venstermans
 */
public interface MomentRepository extends JpaRepository<Moment, Long> {

}
