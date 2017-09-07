package culturelog.rest.repository;

import culturelog.rest.domain.Medium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Jan Venstermans
 */
public interface MediumRepository extends JpaRepository<Medium, Long> {

    List<Medium> findByUserId(Long userId);

    Optional<Medium> findByUserIdAndName(Long userId, String naam);
}
