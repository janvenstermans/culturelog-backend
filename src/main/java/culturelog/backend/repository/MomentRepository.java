package culturelog.backend.repository;

import culturelog.backend.domain.Moment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Jan Venstermans
 */
public interface MomentRepository extends JpaRepository<Moment, Long> {

}
