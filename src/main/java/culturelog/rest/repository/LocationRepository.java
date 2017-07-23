package culturelog.rest.repository;

import culturelog.rest.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Jan Venstermans
 */
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByUserId(Long userId);
}
